/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;

import scala.collection.Iterator;

import com.google.gson.Gson;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.gremlin.jsr223.GremlinScriptEngine;
import com.wordpress.salaboy.tracking.json.ResponseNode;

/**
 *
 * @author salaboy
 */
public class Neo4JSimpleGraphRestTest {

	WrappingNeoServerBootstrapper srv;
	private String baseUri = "http://localhost:7575";
	private AbstractGraphDatabase myDb;
    private HttpClient httpClient;
	private String contentType = "application/json";
	private String createNodeUrlPath = "/db/data/node";
	private String createIndexUrlPath = "/db/data/index/node/";
	private String associateNodeToIndexUrlPath = "/db/data/index/node/";
    
    public enum MyRelationshipTypes implements RelationshipType {

        KNOWS, CREATES, INSTANCIATE, USE
    }

    public Neo4JSimpleGraphRestTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
//    	myDb = new EmbeddedGraphDatabase("test/db/graph2");
    	myDb = new ImpermanentGraphDatabase("var/base2");

        EmbeddedServerConfigurator config = new EmbeddedServerConfigurator(
                myDb );
        config.configuration().setProperty(
                Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575 );
        config.configuration().setProperty(
                Configurator.REST_API_PATH_PROPERTY_KEY, "http://localhost:7575/db/data/" );
        srv = new WrappingNeoServerBootstrapper(
        		myDb, config);
        srv.start();
        this.httpClient = new HttpClient();

    }

    @After
    public void tearDown() {
    	srv.stop();
//    	myDb.shutdown();
    }

    private String createNewNode(Map<String, String> properties) {
		PostMethod method = new PostMethod(this.baseUri
				+ this.createNodeUrlPath);

		for (String key : properties.keySet()) {
			method.addParameter(key, properties.get(key));
		}
		try {
			method.addRequestHeader("Accept", this.contentType);
			this.httpClient.executeMethod(method);
			return method.getResponseHeader("Location").getValue();
		} catch (Exception e) {
			throw new RuntimeException("There was an error creating indexes", e);
		}
	}

    private void associateNodeToIndex(String nodeUrl, String indexName,
			Map<String, String> props) {
		for (String key : props.keySet()) {
			PostMethod method = new PostMethod(this.baseUri
					+ this.associateNodeToIndexUrlPath + indexName + "/" + key
					+ "/" + props.get(key));
			try {
				method.setRequestEntity(new StringRequestEntity("\"" + nodeUrl
						+ "\"", this.contentType, "UTF-8"));
				method.addRequestHeader("Accept", this.contentType);
				method.addRequestHeader("Content-Type", this.contentType);
				this.httpClient.executeMethod(method);
				if (method.getStatusCode() > 300) {
					throw new RuntimeException(
							"There was an error associating node to index."
									+ nodeUrl + " " + indexName);

				}
			} catch (Exception e) {
				throw new RuntimeException(
						"There was an error associating node to index."
								+ nodeUrl + " " + indexName, e);
			}
		}
	}

    
    private String createRelationship(String nodeFrom, String nodeTo,
			String type, Map<String, String> props) {
		try {
			String relationshipUrl = nodeFrom + "/relationships";
			HttpMethod method = new PostMethod(relationshipUrl);
			method.addRequestHeader("Accept", this.contentType);
			method.addRequestHeader("Content-Type", this.contentType);
			String body = "{ \"to\": \"" + nodeTo + "\",\"type\":\"" + type
					+ "\", \"test\":\"a\"}";
			((PostMethod)method).setRequestEntity(new StringRequestEntity(body,
					this.contentType, "UTF-8"));
			this.httpClient.executeMethod(method);
			Gson gson = new Gson();
			ResponseNode node = gson.fromJson(method.getResponseBodyAsString(),
					ResponseNode.class);
			String relationship = node.getSelf();
			method = new PutMethod(relationship + "/" + "properties");
			method.addRequestHeader("Accept", this.contentType);
			method.addRequestHeader("Content-Type", this.contentType);
			body = "{";
			for (String prop : props.keySet()) {
				body += "\"" + prop + "\":\"" + props.get(prop) + "\"";
			}
			body += "}";
			((PutMethod)method).setRequestEntity(new StringRequestEntity(body,
					this.contentType, "UTF-8"));
			this.httpClient.executeMethod(method);
			return node.getSelf();
		} catch (Exception e) {
			throw new RuntimeException(
					"There was an error creating relationship." + nodeFrom
							+ " " + nodeTo, e);
		}
    }
    
    public String getProperty(String node, String name) {
		try {
			GetMethod method = new GetMethod(node + "/properties/" + name);
			method.addRequestHeader("Accept", this.contentType);
			this.httpClient.executeMethod(method);
			return method.getResponseBodyAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
    @Test
    public void simpleGraph() {
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase("var/base");


        Transaction tx = graphDb.beginTx();
        try {
        	Map<String, String> firstNodeProps = new HashMap<String, String>();
        	firstNodeProps.put("message", "Hello world");
        	String firstNode = this.createNewNode(firstNodeProps);
            
        	Map<String, String> secondNodeProps = new HashMap<String, String>();
        	secondNodeProps.put("message", "Hello neo4j");
        	String secondNode = this.createNewNode(secondNodeProps);
        	
        	Map<String, String> relationshipProp = new HashMap<String, String>();
        	relationshipProp.put("message", "relationship");
            String relationshipNode = this.createRelationship(firstNode, secondNode, MyRelationshipTypes.KNOWS.name(), relationshipProp);

            Assert.assertEquals("\"Hello world\"", this.getProperty(firstNode, "message"));
            Assert.assertEquals("\"relationship\"", this.getProperty(relationshipNode, "message"));
            Assert.assertEquals("\"Hello neo4j\"", this.getProperty(secondNode, "message"));
        } finally {
            tx.finish();
            graphDb.shutdown();
        }


    }

    @Test
    @Ignore("Implement when we know how to install plugin to server")
    public void emergencyGraphNeo4jAndGremlin() throws ScriptException {
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase("var/base");
        Transaction tx = graphDb.beginTx();
        Node call = null;
        String vehicle2Id = "";
        try {
            call = graphDb.createNode();

            call.setProperty("callId", "Call:" + UUID.randomUUID().toString());
            call.setProperty("phoneNumber", "555-1234");
            Node emergency = graphDb.createNode();
            emergency.setProperty("emergencyId", "Emergency:" + UUID.randomUUID().toString());
            Relationship callEmergencyRelationship = call.createRelationshipTo(emergency, MyRelationshipTypes.CREATES);
            callEmergencyRelationship.setProperty("time", System.currentTimeMillis());

            Node procedureOne = graphDb.createNode();
            procedureOne.setProperty("procedureName", "DefaultHeartAttack");
            procedureOne.setProperty("procedureId", "Procedure:" + UUID.randomUUID().toString());

            Relationship emergencyProcedureOneRelationship = emergency.createRelationshipTo(procedureOne, MyRelationshipTypes.INSTANCIATE);

            Node procedureTwo = graphDb.createNode();
            procedureTwo.setProperty("procedureName", "GenericBankRobbery");
            procedureTwo.setProperty("procedureId", "Procedure:" + UUID.randomUUID().toString());

            Relationship emergencyProcedureTwoRelationship = emergency.createRelationshipTo(procedureTwo, MyRelationshipTypes.INSTANCIATE);

            Node vehicleOne = graphDb.createNode();
            vehicleOne.setProperty("vehicleId", "Vehicle:" + UUID.randomUUID().toString());
            vehicleOne.setProperty("vehicleType", "Ambulance");

            Relationship procedureVehicleOne = procedureOne.createRelationshipTo(vehicleOne, MyRelationshipTypes.USE);

            Node vehicleTwo = graphDb.createNode();
            vehicleTwo.setProperty("vehicleId", "Vehicle:" + UUID.randomUUID().toString());
            vehicleTwo.setProperty("vehicleType", "Police Car");
            vehicle2Id = (String) vehicleTwo.getProperty("vehicleId");
            Relationship procedureVehicleTwo = procedureOne.createRelationshipTo(vehicleTwo, MyRelationshipTypes.USE);


            tx.success();

//
//            


        } finally {
            tx.finish();
            
        }


        Graph graph = new Neo4jGraph(graphDb);
        ScriptEngine engine = new GremlinScriptEngine();

        List results = new ArrayList();
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("g", graph);
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("v", graph.getVertex(call.getId()));
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("phoneNumber", "555-1234");
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("results", results);

        List<Vertex> result = (List<Vertex>) engine.eval("v.out('CREATES').out('INSTANCIATE').out('USE'){it.vehicleType=='Police Car'} >> results");
        System.out.println("result.size" + result.size());
        for (Vertex vertex : result) {
            System.out.println("Vertex = " + vertex.getProperty("vehicleId"));
            assertEquals(vertex.getProperty("vehicleId"), vehicle2Id);
        }
        graphDb.shutdown();
        graph.shutdown();



    }

    @Test
    @Ignore("Implement when we know how to install plugin to server")
    public void emergencyGraphNeo4jAndCypher() throws ScriptException {
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase("var/base");
        Transaction tx = graphDb.beginTx();
        Node call = null;
        String vehicle2Id = "";
        try {
            call = graphDb.createNode();

            call.setProperty("callId", "Call:" + UUID.randomUUID().toString());
            call.setProperty("phoneNumber", "555-1234");
            Node emergency = graphDb.createNode();
            emergency.setProperty("emergencyId", "Emergency:" + UUID.randomUUID().toString());
            Relationship callEmergencyRelationship = call.createRelationshipTo(emergency, MyRelationshipTypes.CREATES);
            callEmergencyRelationship.setProperty("time", System.currentTimeMillis());

            Node procedureOne = graphDb.createNode();
            procedureOne.setProperty("procedureName", "DefaultHeartAttack");
            procedureOne.setProperty("procedureId", "Procedure:" + UUID.randomUUID().toString());

            Relationship emergencyProcedureOneRelationship = emergency.createRelationshipTo(procedureOne, MyRelationshipTypes.INSTANCIATE);

            Node procedureTwo = graphDb.createNode();
            procedureTwo.setProperty("procedureName", "GenericBankRobbery");
            procedureTwo.setProperty("procedureId", "Procedure:" + UUID.randomUUID().toString());

            Relationship emergencyProcedureTwoRelationship = emergency.createRelationshipTo(procedureTwo, MyRelationshipTypes.INSTANCIATE);

            Node vehicleOne = graphDb.createNode();
            vehicleOne.setProperty("vehicleId", "Vehicle:" + UUID.randomUUID().toString());
            vehicleOne.setProperty("vehicleType", "Ambulance");

            Relationship procedureVehicleOne = procedureOne.createRelationshipTo(vehicleOne, MyRelationshipTypes.USE);

            Node vehicleTwo = graphDb.createNode();
            vehicleTwo.setProperty("vehicleId", "Vehicle:" + UUID.randomUUID().toString());
            vehicleTwo.setProperty("vehicleType", "Police Car");
            vehicle2Id = (String) vehicleTwo.getProperty("vehicleId");
            Relationship procedureVehicleTwo = procedureOne.createRelationshipTo(vehicleTwo, MyRelationshipTypes.USE);


            tx.success();


            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(graphDb);
            Query query = parser.parse("start n=(" + call.getId() + ") where n.phoneNumber = '555-1234' return n");
            ExecutionResult result = engine.execute(query);

            System.out.println("result" + result);
            Iterator<Node> n_column = result.columnAs("n");

            assertEquals(call.getId(), n_column.next().getId());

        } finally {
            tx.finish();
            
            graphDb.shutdown();
            
        }
    }

    @Test
    @Ignore("Implement when we know how to install plugin to server")
    public void neo4JCypherPlusIndexTest() {

        GraphDatabaseService graphDb = new ImpermanentGraphDatabase("var/base");
        Transaction tx = graphDb.beginTx();
        IndexManager index = graphDb.index();
        Index<Node> callsIndex = index.forNodes("calls");
        try {
            Node call1 = graphDb.createNode();
            call1.setProperty("callId", "Call:" + UUID.randomUUID().toString());
            call1.setProperty("phoneNumber", "555-1234");
            callsIndex.add(call1, "phoneNumber", "555-1234");
            Node call2 = graphDb.createNode();
            call2.setProperty("callId", "Call:" + UUID.randomUUID().toString());
            call2.setProperty("phoneNumber", "555-9999");
            callsIndex.add(call2, "phoneNumber", "555-9999");

            tx.success();

            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(graphDb);
            Query query = parser.parse("start n=(calls, 'phoneNumber:555-1234')  return n");
            ExecutionResult result = engine.execute(query);
            System.out.println("Result = "+result);
            System.out.println("Result Size = "+result.size());
            Iterator<Node> n_column = result.columnAs("n");
            assertEquals(call1.getId(), n_column.next().getId());

        } finally {
            tx.finish();
            graphDb.shutdown();
        }
    }
}
