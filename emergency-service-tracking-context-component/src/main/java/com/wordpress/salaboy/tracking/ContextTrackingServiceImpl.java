/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;

import java.util.UUID;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import scala.collection.Iterator;

/**
 *
 * @author salaboy
 */
public class ContextTrackingServiceImpl implements ContextTrackingService {

    private GraphDatabaseService graphDb;
    private IndexManager index;
    private Index<Node> callsIndex;
    private Index<Node> emergenciesIndex;
    private Index<Node> proceduresIndex;
    private Index<Node> vehiclesIndex;
    private Index<Node> channelsIndex;
    public static String defaultDB = "db/graph";
    private static ContextTrackingServiceImpl instance;
    
    public static ContextTrackingService getInstance(){
        if(instance == null){
            instance = new ContextTrackingServiceImpl(new EmbeddedGraphDatabase(defaultDB));
        }
        return instance;
    }
    
    public ContextTrackingServiceImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;

        this.index = this.graphDb.index();
        this.callsIndex = index.forNodes("calls");
        this.emergenciesIndex = index.forNodes("emergencies");
        this.proceduresIndex = index.forNodes("procedures");
        this.vehiclesIndex = index.forNodes("vehicles");
        this.channelsIndex = index.forNodes("channels");
    }

    @Override
    public String newCall() {
        Transaction tx = graphDb.beginTx();
        String callId = "Call-" + UUID.randomUUID().toString();
        try {
            Node call = graphDb.createNode();
            call.setProperty("callId", callId);
            call.setProperty("phoneNumber", "555-1234");
            this.callsIndex.add(call, "callId", callId);
            this.callsIndex.add(call, "phoneNumber", "555-1234");
            tx.success();
        } finally {
            tx.finish();

        }
        return callId;

    }

    @Override
    public String newEmergency() {
        Transaction tx = graphDb.beginTx();
        String emergencyId = "Emergency-" + UUID.randomUUID().toString();
        try {
            Node emergency = graphDb.createNode();
            emergency.setProperty("emergencyId", emergencyId);
            this.emergenciesIndex.add(emergency, "emergencyId", emergencyId);
            tx.success();
        } finally {
            tx.finish();

        }

        return emergencyId;
    }

    @Override
    public void attachEmergency(String callId, String emergencyId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(calls, 'callId:" + callId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node call = n_column.next();

            query = parser.parse("start n=(emergencies, 'emergencyId:" + emergencyId + "')  return n");
            result = engine.execute(query);
            n_column = result.columnAs("n");
            Node emergency = n_column.next();

            call.createRelationshipTo(emergency, EmergencyRelationshipType.CREATES);

            tx.success();
        } finally {
            tx.finish();

        }


    }

    @Override
    public String newProcedure() {
        Transaction tx = graphDb.beginTx();
        String procedureId = "Procedure-" + UUID.randomUUID().toString();
        try {
            Node procedure = graphDb.createNode();
            procedure.setProperty("procedureId", procedureId);
            this.proceduresIndex.add(procedure, "procedureId", procedureId);
            tx.success();
        } finally {
            tx.finish();

        }

        return procedureId;

    }

    @Override
    public void attachProcedure(String emergencyId, String procedureId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(emergencies, 'emergencyId:" + emergencyId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node emergency = n_column.next();

            query = parser.parse("start n=(procedures, 'procedureId:" + procedureId + "')  return n");
            result = engine.execute(query);
            n_column = result.columnAs("n");
            Node procedure = n_column.next();

            emergency.createRelationshipTo(procedure, EmergencyRelationshipType.INSTANTIATE);

            tx.success();
        } finally {
            tx.finish();

        }

    }

    @Override
    public String newVehicle() {
        Transaction tx = graphDb.beginTx();
        String vehicleId = "Vehicle-" + UUID.randomUUID().toString();
        try {
            Node vehicle = graphDb.createNode();
            vehicle.setProperty("vehicleId", vehicleId);
            this.vehiclesIndex.add(vehicle, "vehicleId", vehicleId);
            tx.success();
        } finally {
            tx.finish();

        }

        return vehicleId;

    }

    @Override
    public void attachVehicle(String procedureId, String vehicleId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(procedures, 'procedureId:" + procedureId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node procedure = n_column.next();

            query = parser.parse("start n=(vehicles, 'vehicleId:" + vehicleId + "')  return n");
            result = engine.execute(query);
            n_column = result.columnAs("n");
            Node vehicle = n_column.next();

            procedure.createRelationshipTo(vehicle, EmergencyRelationshipType.USE);

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public String newServiceChannel() {
        Transaction tx = graphDb.beginTx();
        String channelId = "Channel-" + UUID.randomUUID().toString();
        try {
            Node channel = graphDb.createNode();
            channel.setProperty("channelId", channelId);
            this.channelsIndex.add(channel, "channelId", channelId);
            tx.success();
        } finally {
            tx.finish();

        }

        return channelId;

    }

    @Override
    public void attachServiceChannel(String emergencyId, String channelId) {
         Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(emergencies, 'emergencyId:" + emergencyId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node emergency = n_column.next();

            query = parser.parse("start n=(channels, 'channelId:" + channelId + "')  return n");
            result = engine.execute(query);
            n_column = result.columnAs("n");
            Node channel = n_column.next();

            emergency.createRelationshipTo(channel, EmergencyRelationshipType.CONSUME);

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public void detachVehicle(String vehicleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void detachProcedure(String procedureId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void detachEmergency(String emergencyId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
