/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.procedures.tracking;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingProvider.ContextTrackingServiceType;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.grid.*;
import com.wordpress.salaboy.messaging.*;
import com.wordpress.salaboy.model.*;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.hornetq.api.core.HornetQException;
import org.jbpm.task.AsyncTaskService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.Node;
import scala.collection.Iterator;

/**
 *
 * @author salaboy
 */
public class GenericEmergencyProcedureWithTrackingTest extends GridBaseTest {

    private MessageConsumer consumer;
    private AsyncTaskService client;
    private MessageConsumerWorker asynchProcedureStartWorker;
    private PersistenceService persistenceService;
    private ContextTrackingService trackingService;

    public GenericEmergencyProcedureWithTrackingTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {


        deleteRecursively(new File(ContextTrackingProvider.defaultDB));
        deleteRecursively(new File("/data"));


//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
//        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
//        persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);
//        trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType) conf.getParameters().get("ContextTrackingImplementation"));

        persistenceService = PersistenceServiceProvider.getPersistenceService();
        trackingService = ContextTrackingProvider.getTrackingService();




    }

    @After
    public void tearDown() throws Exception {


        
        ContextTrackingProvider.clear();
        PersistenceServiceProvider.clear();
    }

    @Test
    public void generic1Test() throws HornetQException, InterruptedException, IOException, ClassNotFoundException {
        System.out.println("Running Tests! ");
        Emergency e = new Emergency();
        Call call = new Call();
        persistenceService.storeCall(call);
        persistenceService.storeEmergency(e);
        assertEquals(1, persistenceService.getAllEmergencies().size()); 
        trackingService.attachEmergency(call.getId(), e.getId());
        assertEquals(0, persistenceService.getAllVehicles().size());
        
        assertEquals(0, persistenceService.getAllProcedures().size());
        Procedure procedure = new Procedure("MyProcedure");
        persistenceService.storeProcedure(procedure);
        assertEquals(1, persistenceService.getAllProcedures().size());
        procedure.setProcessInstanceId(12L);
        persistenceService.storeProcedure(procedure);
        assertEquals(1, persistenceService.getAllProcedures().size());
        
        trackingService.attachProcedure(e.getId(), procedure.getId());
        
        

        CypherParser parser = new CypherParser();

        ExecutionEngine engine = new ExecutionEngine(trackingService.getGraphDb());

        Query query = parser.parse("start n=(emergencies, 'emergencyId:*')  return n");

        ExecutionResult result = engine.execute(query);
        Iterator<Node> n_column = result.columnAs("n");


        System.out.println("results: " + result);
        while (n_column.hasNext()) {
            Node currentNode = n_column.next();
            for (String key : currentNode.getPropertyKeys()) {
                System.out.println("Property (" + key + "): " + currentNode.getProperty(key));
            }
        }
        assertEquals(1, result.size());
        
        query = parser.parse("start n=(procedures, 'procedureId:*')  return n");

        result = engine.execute(query);
        n_column = result.columnAs("n");


        System.out.println("results: " + result);
        while (n_column.hasNext()) {
            Node currentNode = n_column.next();
            for (String key : currentNode.getPropertyKeys()) {
                System.out.println("Property (" + key + "): " + currentNode.getProperty(key));
            }
        }
        assertEquals(1, result.size());

    }

    @Test
    public void generic2Test() throws HornetQException, InterruptedException, IOException, ClassNotFoundException {
        System.out.println("Running Tests! ");
        Emergency e = new Emergency();
        Call call = new Call();
        persistenceService.storeCall(call);
        persistenceService.storeEmergency(e);
        assertEquals(1, persistenceService.getAllEmergencies().size()); 
        trackingService.attachEmergency(call.getId(), e.getId());
        assertEquals(0, persistenceService.getAllVehicles().size());
       
        assertEquals(0, persistenceService.getAllProcedures().size());
        Procedure procedure = new Procedure("MyProcedure");
        persistenceService.storeProcedure(procedure);
        assertEquals(1, persistenceService.getAllProcedures().size());
        procedure.setProcessInstanceId(12L);
        persistenceService.storeProcedure(procedure);
        assertEquals(1, persistenceService.getAllProcedures().size());

        trackingService.attachProcedure(e.getId(), procedure.getId());
        
        CypherParser parser = new CypherParser();

        ExecutionEngine engine = new ExecutionEngine(trackingService.getGraphDb());

        Query query = parser.parse("start n=(emergencies, 'emergencyId:*')  return n");

        ExecutionResult result = engine.execute(query);
        Iterator<Node> n_column = result.columnAs("n");


        System.out.println("results: " + result);
        while (n_column.hasNext()) {
            Node currentNode = n_column.next();
            for (String key : currentNode.getPropertyKeys()) {
                System.out.println("Property (" + key + "): " + currentNode.getProperty(key));
            }
        }
        assertEquals(1, result.size());
        
        query = parser.parse("start n=(procedures, 'procedureId:*')  return n");

        result = engine.execute(query);
        n_column = result.columnAs("n");


        System.out.println("results: " + result);
        while (n_column.hasNext()) {
            Node currentNode = n_column.next();
            for (String key : currentNode.getPropertyKeys()) {
                System.out.println("Property (" + key + "): " + currentNode.getProperty(key));
            }
        }
        assertEquals(1, result.size());
    }

    private static void deleteRecursively(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            throw new RuntimeException(
                    "Couldn't empty database. Offending file:" + file);
        }
    }
}