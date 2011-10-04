/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.ServiceChannel;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.Vehicle.VehicleType;
import org.neo4j.cypher.commands.Query;
import org.neo4j.graphdb.Node;
import scala.collection.Iterator;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FireTruck;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;
import java.util.Date;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
    public void simpleAPIPlusCypherQueryTest() {
       
        Call call = new Call(1, 1, new Date());
        DistributedPeristenceServerService.getInstance().storeCall(call);
        assertNotSame("", call.getId());

        call = DistributedPeristenceServerService.getInstance().loadCall(call.getId());
        assertNotNull(call);

        Emergency emergency = new Emergency();
        DistributedPeristenceServerService.getInstance().storeEmergency(emergency);
        assertNotSame("", emergency.getId());

        emergency = DistributedPeristenceServerService.getInstance().loadEmergency(emergency.getId());
        assertNotNull(emergency);

        ContextTrackingServiceImpl.getInstance().attachEmergency(call.getId(), emergency.getId());

        Procedure procedure = new Procedure("MyProcedure");
        DistributedPeristenceServerService.getInstance().storeProcedure(procedure);
        assertNotSame("", procedure.getId());

        procedure = DistributedPeristenceServerService.getInstance().loadProcedure(procedure.getId());
        assertNotNull(procedure);

        ContextTrackingServiceImpl.getInstance().attachProcedure(emergency.getId(), procedure.getId());


        Vehicle vehicle = new Ambulance();

        DistributedPeristenceServerService.getInstance().storeVehicle(vehicle);
        assertNotSame("", vehicle.getId());

        vehicle = DistributedPeristenceServerService.getInstance().loadVehicle(vehicle.getId());
        assertNotNull(vehicle);


        ContextTrackingServiceImpl.getInstance().attachVehicle(procedure.getId(), vehicle.getId());

        Vehicle vehicle2 = new FireTruck();
        DistributedPeristenceServerService.getInstance().storeVehicle(vehicle2);
        assertNotSame("", vehicle2.getId());

        vehicle2 = DistributedPeristenceServerService.getInstance().loadVehicle(vehicle2.getId());
        assertNotNull(vehicle2);

        ContextTrackingServiceImpl.getInstance().attachVehicle(procedure.getId(), vehicle2.getId());

        ServiceChannel channel = new ServiceChannel("MyChannel");
        DistributedPeristenceServerService.getInstance().storeServiceChannel(channel);
        assertNotSame("", channel.getId());
        
        channel = DistributedPeristenceServerService.getInstance().loadServiceChannel(channel.getId());
        assertNotNull(channel);
        
        ContextTrackingServiceImpl.getInstance().attachServiceChannel(emergency.getId(), channel.getId());


        CypherParser parser = new CypherParser();
        ExecutionEngine engine = new ExecutionEngine(ContextTrackingServiceImpl.getInstance().getGraphDb());


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
