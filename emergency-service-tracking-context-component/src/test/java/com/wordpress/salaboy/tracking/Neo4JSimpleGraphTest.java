/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;

import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.cypher.commands.Query;
import scala.collection.Iterator;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.javacompat.CypherParser;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.gremlin.jsr223.GremlinScriptEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
public class Neo4JSimpleGraphTest {

    public enum MyRelationshipTypes implements RelationshipType {

        KNOWS, CREATES, INSTANCIATE, USE
    }

    public Neo4JSimpleGraphTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void simpleGraph() {
        GraphDatabaseService graphDb = new ImpermanentGraphDatabase("var/base");


        Transaction tx = graphDb.beginTx();
        try {
            Node firstNode = graphDb.createNode();
            Node secondNode = graphDb.createNode();
            Relationship relationship = firstNode.createRelationshipTo(secondNode, MyRelationshipTypes.KNOWS);

            firstNode.setProperty("message", "Hello, ");
            secondNode.setProperty("message", "world!");
            relationship.setProperty("message", "brave Neo4j ");
            tx.success();

            System.out.print(firstNode.getProperty("message"));
            System.out.print(relationship.getProperty("message"));
            System.out.print(secondNode.getProperty("message"));
        } finally {
            tx.finish();
            graphDb.shutdown();
        }


    }

    @Test
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
