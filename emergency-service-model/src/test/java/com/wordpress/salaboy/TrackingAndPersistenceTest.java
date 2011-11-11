/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;

import scala.collection.Iterator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.context.tracking.json.QueryResult;
import com.wordpress.salaboy.context.tracking.json.ResponseNode;
import com.wordpress.salaboy.model.vehicles.Ambulance;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.vehicles.FireTruck;
import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.ServiceChannel;
import com.wordpress.salaboy.model.vehicles.Vehicle;
import com.wordpress.salaboy.model.persistence.PersistenceService;
import com.wordpress.salaboy.model.persistence.PersistenceServiceProvider;

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
        
        PersistenceServiceProvider.clear();
        ContextTrackingProvider.clear();
    }

    @Test
    public void simpleAPIPlusCypherQueryTest() throws IOException {
        PersistenceServiceProvider.configFile = "local-config-beans.xml";
        ContextTrackingProvider.configFile = "local-config-beans.xml";
        PersistenceService persistenceService = PersistenceServiceProvider.getPersistenceService();
        ContextTrackingService trackingService = ContextTrackingProvider.getTrackingService();
        
        
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


        query = parser.parse("start v=(vehicles, 'vehicleId:" + vehicle.getId() + "')  match (v) <-[USE]- (w)    return w");
        
        //query = parser.parse("start s=(procedures, 'procedureId:" + procedure.getId() + "')  match (s) <-[SUB]- (p)    return p");

        result = engine.execute(query);
        n_column = result.columnAs("w");
        
        assertEquals(1, result.size());
        
         while (n_column.hasNext()) {
            Node currentNode = n_column.next();
            for (String key : currentNode.getPropertyKeys()) {
                System.out.println("Property (" + key + "): " + currentNode.getProperty(key));
            }
        }
        
    }
    
    @Test
    public void simpleAPIPlusCypherQueryRestTest() throws IOException {
    	//Start wrapping server
    	ImpermanentGraphDatabase myDb = new ImpermanentGraphDatabase();

		EmbeddedServerConfigurator config = new EmbeddedServerConfigurator(myDb);
		config.configuration().setProperty(
				Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);
		config.configuration().setProperty(
				Configurator.REST_API_PATH_PROPERTY_KEY,
				"http://localhost:7575/db/data/");
		WrappingNeoServerBootstrapper srv = new WrappingNeoServerBootstrapper(myDb, config);
		srv.start();

        PersistenceServiceProvider.configFile = "remote-config-beans.xml";
        ContextTrackingProvider.configFile = "remote-config-beans.xml";
        PersistenceService persistenceService = PersistenceServiceProvider.getPersistenceService();
        ContextTrackingService trackingService = ContextTrackingProvider.getTrackingService();
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
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod("http://localhost:7575/db/data/ext/CypherPlugin/graphdb/execute_query");
        method.setRequestHeader("Content-type", "application/json");
        method.setRequestHeader("Accept", "application/json");
        String content = "{\"query\": \"start n=(calls, 'callId:" + call.getId() + "')  match (n)-[r:CREATES]->(x)-[i:INSTANTIATE]-> (w) -[u:USE]->v  return v\"}";
        method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
        client.executeMethod(method);
        
        Gson gson = new Gson();

		QueryResult result = gson.fromJson(method.getResponseBodyAsString(),
				new TypeToken<QueryResult>() {
				}.getType());

		System.out.println("results: " + result);
		Assert.assertEquals(2, result.getData().size());
		for (List<ResponseNode> data : result.getData()) {
			Map<String, String> props = data.get(0).getData();
			for (String key : props.keySet()) {
				System.out.println("Property ("+key+"): "+props.get(key));
			}
		}

        client = new HttpClient();
        method = new PostMethod("http://localhost:7575/db/data/ext/CypherPlugin/graphdb/execute_query");
        method.setRequestHeader("Content-type", "application/json");
        method.setRequestHeader("Accept", "application/json");
        content = "{\"query\": \"start v=(vehicles, 'vehicleId:" + vehicle.getId() + "')  match (v) <-[USE]- (w)    return w\"}";
        method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
        client.executeMethod(method);
        
        gson = new Gson();

		result = gson.fromJson(method.getResponseBodyAsString(),
				new TypeToken<QueryResult>() {
				}.getType());
                
		System.out.println("results: " + result);
		Assert.assertEquals(1, result.getData().size());
		for (List<ResponseNode> data : result.getData()) {
			Map<String, String> props = data.get(0).getData();
			for (String key : props.keySet()) {
				System.out.println("Property ("+key+"): "+props.get(key));
			}
		}

		
//        tracking.detachVehicle(vehicleId);
//        
//        tracking.detachProcedure(procedureId);
//        
//        tracking.detachEmergency(emergencyId);
//        

        myDb.shutdown();
        srv.stop();
        

    }
}
