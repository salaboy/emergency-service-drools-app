/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;


import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingProvider.ContextTrackingServiceType;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.model.*;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider.PersistenceServiceType;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.*;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import scala.collection.Iterator;

/**
 *
 * @author salaboy
 */
public class TrackingAndPersistenceTest {

    private GraphDatabaseService graphDb;

    public TrackingAndPersistenceTest() {
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
    public void simpleAPIPlusCypherQueryTest() throws IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
        PersistenceService persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceType.DISTRIBUTED_MAP, conf);
        Call call = new Call(1, 1, new Date());
        persistenceService.storeCall(call);
        
        assertNotSame("", call.getId());

        call = persistenceService.loadCall(call.getId());
        assertNotNull(call);

        Emergency emergency = new Emergency();
        persistenceService.storeEmergency(emergency);
        assertNotSame("", emergency.getId());

        emergency = persistenceService.loadEmergency(emergency.getId());
        assertNotNull(emergency);
        ContextTrackingService trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingServiceType)conf.getParameters().get("ContextTrackingImplementation"));
        trackingService.attachEmergency(call.getId(), emergency.getId());

        Procedure procedure = new Procedure("MyProcedure");
        persistenceService.storeProcedure(procedure);
        assertNotSame("", procedure.getId());

        procedure = persistenceService.loadProcedure(procedure.getId());
        assertNotNull(procedure);

        trackingService.attachProcedure(emergency.getId(), procedure.getId());


        Vehicle vehicle = new Ambulance();

        persistenceService.storeVehicle(vehicle);
        assertNotSame("", vehicle.getId());

        vehicle = persistenceService.loadVehicle(vehicle.getId());
        assertNotNull(vehicle);


        trackingService.attachVehicle(procedure.getId(), vehicle.getId());

        Vehicle vehicle2 = new FireTruck();
        persistenceService.storeVehicle(vehicle2);
        assertNotSame("", vehicle2.getId());

        vehicle2 = persistenceService.loadVehicle(vehicle2.getId());
        assertNotNull(vehicle2);

        trackingService.attachVehicle(procedure.getId(), vehicle2.getId());

        ServiceChannel channel = new ServiceChannel("MyChannel");
        persistenceService.storeServiceChannel(channel);
        assertNotSame("", channel.getId());
        
        channel = persistenceService.loadServiceChannel(channel.getId());
        assertNotNull(channel);
        
        trackingService.attachServiceChannel(emergency.getId(), channel.getId());


        CypherParser parser = new CypherParser();
        ExecutionEngine engine = new ExecutionEngine(trackingService.getGraphDb());


        //Give me all the vehicle associated with the procedures that are part of the emergency that was created by this phoneCallId
        Query query = parser.parse("start n=(calls, 'callId:" + call.getId() + "')  match (n)-[r:CREATES]->(x)-[i:INSTANTIATE]-> (w) -[u:USE]->v  return v");

        ExecutionResult result = engine.execute(query);
        Iterator<Node> n_column = result.columnAs("v");


        System.out.println("results: " + result);
        assertEquals(2, result.size());
        while (n_column.hasNext()) {
            Node currentNode = n_column.next();
            for (String key : currentNode.getPropertyKeys()) {
                System.out.println("Property (" + key + "): " + currentNode.getProperty(key));
            }
        }


        //ContextTrackingServiceImpl.getInstance().shutdown();


    }
}
