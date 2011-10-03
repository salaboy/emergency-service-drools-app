/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;



import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.gremlin.jsr223.GremlinScriptEngine;
import com.wordpress.salaboy.tracking.json.QueryResult;
import com.wordpress.salaboy.tracking.json.ResponseNode;

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
    	srv.stop();
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

//        Give me all the vehicle associated with the procedures that are part of the emergency that was created by this phoneCallId
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(this.baseUri  + "/db/data/ext/CypherPlugin/graphdb/execute_query");
        method.setRequestHeader("Content-type", "application/json");
        method.setRequestHeader("Accept", "application/json");
        String content = "{\"query\": \"start n=(calls, 'callId:" + callId + "')  match (n)-[r:CREATES]->(x)-[i:INSTANTIATE]-> (w) -[u:USE]->v  return v\"}";
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
    public void simpleAPIPlusGremlinQueryTest() throws Exception {
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

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(this.baseUri  + "/db/data/ext/GremlinPlugin/graphdb/execute_script");
        method.setRequestHeader("Content-type", "application/json");
        method.setRequestHeader("Accept", "application/json");
        
        String content = "{\"script\": \"g.idx('calls').get('callId','"+callId+"')[0].out('CREATES').out('INSTANTIATE').out('USE')\"}";
        method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
        
        client.executeMethod(method);
        Gson gson = new Gson();
        List<ResponseNode> result = gson.fromJson(method.getResponseBodyAsString(),
				new TypeToken<List<ResponseNode>>() {
				}.getType());
        Assert.assertEquals(2, result.size());
        for (ResponseNode responseNode : result) {
        	System.out.println("Vertex = " + responseNode.getData().get("vehicleId"));
		}
    }
    
}
