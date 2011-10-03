/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;



import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.parser.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;

import scala.collection.Iterator;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.gremlin.jsr223.GremlinScriptEngine;

/**
 *
 * @author salaboy
 */
public class TrackingServiceSimpleRestTest {

    public TrackingServiceSimpleRestTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    WrappingNeoServerBootstrapper srv;
	private String baseUri = "http://localhost:7575";
    private AbstractGraphDatabase myDb; 
    @Before
    public void setUp() {
//    	myDb = new EmbeddedGraphDatabase("test/db/graph1");
    	myDb = new ImpermanentGraphDatabase("var/base1");

        EmbeddedServerConfigurator config = new EmbeddedServerConfigurator(
                myDb );
        config.configuration().setProperty(
                Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575 );
        config.configuration().setProperty(
                Configurator.REST_API_PATH_PROPERTY_KEY, "http://localhost:7575/db/data/" );
        srv = new WrappingNeoServerBootstrapper(
        		myDb, config);
        srv.start();
    }

    @After
    public void tearDown() {
//    	srv.stop();
    	myDb.shutdown();
    }

    @Test
    public void simpleAPIPlusCypherQueryTest() throws Exception {
        
        ContextTrackingService tracking = new ContextTrackingServiceRest(this.baseUri);

        String callId = tracking.newCall();

        String emergencyId = tracking.newEmergency();
        tracking.attachEmergency(callId, emergencyId);

        String procedureId = tracking.newProcedure();
        tracking.attachProcedure(emergencyId, procedureId);

        String vehicleId = tracking.newVehicle();
        tracking.attachVehicle(procedureId, vehicleId);
        
        String vehicle2Id = tracking.newVehicle();
        tracking.attachVehicle(procedureId, vehicle2Id);

        String channelId = tracking.newServiceChannel();

        tracking.attachServiceChannel(emergencyId, channelId);


        CypherParser parser = new CypherParser();
        ExecutionEngine engine = new ExecutionEngine(myDb);

        //TODO cypher not working yet through server, so check the results directly into the DB. 
        //Give me all the vehicle associated with the procedures that are part of the emergency that was created by this phoneCallId
//        HttpClient client = new HttpClient();
//        PostMethod method = new PostMethod(this.baseUri  + "/db/data/ext/CypherPlugin/graphdb/execute_query");
//        method.setRequestHeader("Content-type", "application/json");
//        method.setRequestHeader("Accept", "application/json");
//        String content = "{\"query\": \"start n=(calls, 'callId:" + callId + "')  match (n)-[r:CREATES]->(x)-[i:INSTANTIATE]-> (w) -[u:USE]->v  return v\"}";
//        method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
//        client.executeMethod(method);
//        
//        Gson gson = new Gson();
//
//		QueryResult result = gson.fromJson(method.getResponseBodyAsString(),
//				new TypeToken<QueryResult>() {
//				}.getType());
//
//		assertEquals(2, result.getData().length);
		
        Query query = parser.parse("start n=(calls, 'callId:" + callId + "')  match (n)-[r:CREATES]->(x)-[i:INSTANTIATE]-> (w) -[u:USE]->v  return v");
        ExecutionResult result = engine.execute(query);
        Iterator<Node> n_column = result.columnAs("v");
        
        
        System.out.println("results: " + result);
        Assert.assertEquals(2, result.size());
        while(n_column.hasNext()){
            Node currentNode = n_column.next();
            for(String key : currentNode.getPropertyKeys()){
                System.out.println("Property ("+key+"): "+currentNode.getProperty(key));
            }
        }
        
        ContextTrackingSimpleGraphServiceRest graphRest = new ContextTrackingSimpleGraphServiceRest(baseUri);
        System.out.println(graphRest.graphEmergency(emergencyId));

        



//        tracking.detachVehicle(vehicleId);
//        
//        tracking.detachProcedure(procedureId);
//        
//        tracking.detachEmergency(emergencyId);
//        

        this.myDb.shutdown();


    }
    
    @Test
    @Ignore("Implement when we know how to install plugin to server")
    public void simpleAPIPlusGremlinQueryTest() throws ScriptException {
//        ContextTrackingService tracking = new ContextTrackingServiceRest(this.baseUri);
    	ContextTrackingService tracking = new ContextTrackingServiceRest(this.baseUri);

        String callId = tracking.newCall();

        String emergencyId = tracking.newEmergency();
        tracking.attachEmergency(callId, emergencyId);

        String procedureId = tracking.newProcedure();
        tracking.attachProcedure(emergencyId, procedureId);

        String vehicleId = tracking.newVehicle();
        tracking.attachVehicle(procedureId, vehicleId);
        
        String vehicle2Id = tracking.newVehicle();
        tracking.attachVehicle(procedureId, vehicle2Id);

        String channelId = tracking.newServiceChannel();

        tracking.attachServiceChannel(emergencyId, channelId);

        Graph graph = new Neo4jGraph(myDb);
        ScriptEngine engine = new GremlinScriptEngine();

        List results = new ArrayList();
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("graph", graph);
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("results", results);
        // Can I reduce the verbosity of accessing the index using a variable?
        List<Vertex> result = (List<Vertex>) engine.eval("graph.idx('calls').get('callId','"+callId+"')[0].out('CREATES').out('INSTANTIATE').out('USE') >> results");
        System.out.println("result.size" + result.size());
        Assert.assertEquals(2, result.size());
        for (Vertex vertex : result) {
            System.out.println("Vertex = " + vertex.getProperty("vehicleId"));
        }
        myDb.shutdown();
        graph.shutdown();
    }
    
}
