/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.gremlin.jsr223.GremlinScriptEngine;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.parser.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.test.ImpermanentGraphDatabase;
import scala.collection.Iterator;
import static org.junit.Assert.*;

/**
 *
 * @author salaboy
 */
public class TrackingServiceSimpleTest {

    private GraphDatabaseService graphDb;

    public TrackingServiceSimpleTest() {
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
    public void simpleAPIPlusCypherQueryTest() {
        this.graphDb = new ImpermanentGraphDatabase("target/emerency-services/base");
        ContextTrackingService tracking = new ContextTrackingServiceImpl(this.graphDb);

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
        ExecutionEngine engine = new ExecutionEngine(this.graphDb);


        //Give me all the vehicle associated with the procedures that are part of the emergency that was created by this phoneCallId
        Query query = parser.parse("start n=(calls, 'callId:" + callId + "')  match (n)-[r:CREATES]->(x)-[i:INSTANTIATE]-> (w) -[u:USE]->v  return v");
        ExecutionResult result = engine.execute(query);
        Iterator<Node> n_column = result.columnAs("v");
        
        
        System.out.println("results: " + result);
        assertEquals(2, result.size());
        while(n_column.hasNext()){
            Node currentNode = n_column.next();
            for(String key : currentNode.getPropertyKeys()){
                System.out.println("Property ("+key+"): "+currentNode.getProperty(key));
            }
        }
        
        

        



//        tracking.detachVehicle(vehicleId);
//        
//        tracking.detachProcedure(procedureId);
//        
//        tracking.detachEmergency(emergencyId);
//        

        this.graphDb.shutdown();


    }
    
    @Test
    public void simpleAPIPlusGremlinQueryTest() throws ScriptException {
        this.graphDb = new ImpermanentGraphDatabase("target/emerency-services/base");
        ContextTrackingService tracking = new ContextTrackingServiceImpl(this.graphDb);

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

        Graph graph = new Neo4jGraph(graphDb);
        ScriptEngine engine = new GremlinScriptEngine();

        List results = new ArrayList();
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("graph", graph);
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("results", results);
        // Can I reduce the verbosity of accessing the index using a variable?
        List<Vertex> result = (List<Vertex>) engine.eval("graph.idx('calls').get('callId','"+callId+"')[0].out('CREATES').out('INSTANTIATE').out('USE') >> results");
        System.out.println("result.size" + result.size());
        assertEquals(2, result.size());
        for (Vertex vertex : result) {
            System.out.println("Vertex = " + vertex.getProperty("vehicleId"));
        }
        graphDb.shutdown();
        graph.shutdown();
    }
    
}
