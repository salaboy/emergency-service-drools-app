/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.context.tracking;

import java.util.UUID;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
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
    private Index<Node> buildingsIndex;
    private Index<Node> patientsIndex;

    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    public ContextTrackingServiceImpl(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
        this.index = this.graphDb.index();
        this.callsIndex = index.forNodes("calls");
        this.emergenciesIndex = index.forNodes("emergencies");
        this.proceduresIndex = index.forNodes("procedures");
        this.vehiclesIndex = index.forNodes("vehicles");
        this.channelsIndex = index.forNodes("channels");
        this.buildingsIndex = index.forNodes("buildings");
        this.patientsIndex = index.forNodes("patients");
    }

    @Override
    public String newCallId() {

        Transaction tx = graphDb.beginTx();
        String callId = "Call-" + UUID.randomUUID().toString();
        try {
            Node callNode = graphDb.createNode();
            callNode.setProperty("callId", callId);
            callNode.setProperty("name", callId); //??? DO I REALLY NEED THIS??
            this.callsIndex.add(callNode, "callId", callId);
            tx.success();
        } finally {
            tx.finish();

        }


        return callId;

    }

    @Override
    public String newEmergencyId() {

        Transaction tx = graphDb.beginTx();
        String emergencyId = "Emergency-" + UUID.randomUUID().toString();
        try {
            Node emergencyNode = graphDb.createNode();
            emergencyNode.setProperty("emergencyId", emergencyId);
            emergencyNode.setProperty("name", emergencyId);
            this.emergenciesIndex.add(emergencyNode, "emergencyId", emergencyId);
            tx.success();
        } finally {
            tx.finish();

        }
        return emergencyId;
    }

    @Override
    public String newEmergencyEntityBuildingId() {
        Transaction tx = graphDb.beginTx();
        String buildingId = "EntityBuilding-" + UUID.randomUUID().toString();
        try {
            Node buildingNode = graphDb.createNode();
            buildingNode.setProperty("buildingId", buildingId);
            buildingNode.setProperty("name", buildingId);
            this.buildingsIndex.add(buildingNode, "buildingId", buildingId);
            tx.success();
        } finally {
            tx.finish();

        }
        return buildingId;
    }

    @Override
    public String newPatientId() {
        Transaction tx = graphDb.beginTx();
        String patientId = "Patient-" + UUID.randomUUID().toString();
        try {
            Node patientNode = graphDb.createNode();
            patientNode.setProperty("patientId", patientId);
            patientNode.setProperty("name", patientId);
            this.patientsIndex.add(patientNode, "patientId", patientId);
            tx.success();
        } finally {
            tx.finish();

        }
        return patientId;
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
    public String newProcedureId() {

        Transaction tx = graphDb.beginTx();
        String procedureId = "Procedure-" + UUID.randomUUID().toString();
        try {
            Node procedureNode = graphDb.createNode();
            procedureNode.setProperty("procedureId", procedureId);
            procedureNode.setProperty("name", procedureId);
            this.proceduresIndex.add(procedureNode, "procedureId", procedureId);
            tx.success();
        } finally {
            tx.finish();

        }

        return procedureId;

    }

    @Override
    public void attachProcedure(String emergencyId, String procedureId) {
        System.out.println(">>>> Attaching Procedure " + procedureId + " to Emergency " + emergencyId);
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
    public String getProcedureAttachedToVehicle(String vehicleId) {
        Transaction tx = graphDb.beginTx();
        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start v=(vehicles, 'vehicleId:" + vehicleId + "')  match (v) <-[USE]- (w)    return w");
            ExecutionResult result = engine.execute(query);

            if (result.isEmpty()) {
                throw new IllegalStateException("No Procedure attached to the Vehicle " + vehicleId);
            }

            Iterator<Node> n_column = result.columnAs("w");
            Node procedure = n_column.next();

            String procedureId = (String) procedure.getProperty("procedureId");

            tx.success();

            return procedureId;

        } finally {
            tx.finish();

        }
    }

    @Override
    public String getCallAttachedToEmergency(String emergencyId) {
        Transaction tx = graphDb.beginTx();
        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start c=(emergencies, 'emergencyId:" + emergencyId + "')  match (e) <-[CREATES]- (c)    return c");
            ExecutionResult result = engine.execute(query);

            if (result.isEmpty()) {
                throw new IllegalStateException("No Call attached to the Emergency " + emergencyId);
            }

            Iterator<Node> n_column = result.columnAs("c");
            Node call = n_column.next();

            String callId = (String) call.getProperty("callId");

            tx.success();

            return callId;

        } finally {
            tx.finish();

        }
    }

    @Override
    public String getEmergencyAttachedToCall(String callId) {
        Transaction tx = graphDb.beginTx();
        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start e=(calls, 'callId:" + callId + "')  match (c) -[CREATES]-> (e)    return e");
            ExecutionResult result = engine.execute(query);

            if (result.isEmpty()) {
                throw new IllegalStateException("No Emergency attached to the Call " + callId);
            }

            Iterator<Node> n_column = result.columnAs("e");
            Node call = n_column.next();

            String emergencyId = (String) call.getProperty("emergencyId");

            tx.success();

            return emergencyId;

        } finally {
            tx.finish();

        }
    }

    @Override
    public String newVehicleId() {


        Transaction tx = graphDb.beginTx();
        String vehicleId = "Vehicle-" + UUID.randomUUID().toString();

        try {
            Node vehicleNode = graphDb.createNode();
            vehicleNode.setProperty("vehicleId", vehicleId);
            vehicleNode.setProperty("name", vehicleId);
            this.vehiclesIndex.add(vehicleNode, "vehicleId", vehicleId);
            tx.success();
        } finally {
            tx.finish();

        }

        return vehicleId;

    }

    @Override
    public void attachVehicle(String procedureId, String vehicleId) {
        System.out.println(">>>> Attaching Vehicle " + vehicleId + " to Procedure " + procedureId);
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
    public void attachProcedures(String parentProcedureId, String childProcedureId) {
        System.out.println(">>>> Attaching Procedure " + childProcedureId + " to Procedure " + parentProcedureId);
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(procedures, 'procedureId:" + parentProcedureId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node parentProcedure = n_column.next();

            query = parser.parse("start n=(procedures, 'procedureId:" + childProcedureId + "')  return n");
            result = engine.execute(query);
            n_column = result.columnAs("n");
            Node childProcedure = n_column.next();

            parentProcedure.createRelationshipTo(childProcedure, EmergencyRelationshipType.SUB);

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public String newServiceChannelId() {

        Transaction tx = graphDb.beginTx();
        String channelId = "Channel-" + UUID.randomUUID().toString();
        try {
            Node channelNode = graphDb.createNode();
            channelNode.setProperty("channelId", channelId);
            channelNode.setProperty("name", channelId);
            this.channelsIndex.add(channelNode, "channelId", channelId);
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
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(vehicles, 'vehicleId:" + vehicleId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node vehicle = n_column.next();
            //Removing the node from the index
            this.vehiclesIndex.remove(vehicle);
            for (Relationship rel : vehicle.getRelationships()) {
                rel.delete();
            }
            //Deleting the node from the graph
            vehicle.delete();

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public void detachProcedure(String procedureId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(procedures, 'procedureId:" + procedureId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node procedure = n_column.next();
            //Removing the node from the index
            this.proceduresIndex.remove(procedure);
            for (Relationship rel : procedure.getRelationships()) {
                rel.delete();
            }
            //Deleting the node from the graph
            procedure.delete();

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public void detachEmergency(String emergencyId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(emergencies, 'emergencyId:" + emergencyId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node emergency = n_column.next();
            //Removing the node from the index
            this.emergenciesIndex.remove(emergency);
            for (Relationship rel : emergency.getRelationships()) {
                rel.delete();
            }
            //Deleting the node from the graph
            emergency.delete();

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public void detachServiceChannel(String serviceChannelId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(channels, 'channelId:" + serviceChannelId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node channel = n_column.next();
            //Removing the node from the index
            this.channelsIndex.remove(channel);
            for (Relationship rel : channel.getRelationships()) {
                rel.delete();
            }
            //Deleting the node from the graph
            channel.delete();

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public void detachPatient(String patientId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(patients, 'patientId:" + patientId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node patient = n_column.next();
            //Removing the node from the index
            this.channelsIndex.remove(patient);
            for (Relationship rel : patient.getRelationships()) {
                rel.delete();
            }
            //Deleting the node from the graph
            patient.delete();

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public void detachEmergencyEntityBuilding(String entityBuildingId) {
        Transaction tx = graphDb.beginTx();

        try {
            CypherParser parser = new CypherParser();
            ExecutionEngine engine = new ExecutionEngine(this.graphDb);
            Query query = parser.parse("start n=(buildings, 'buildingId:" + entityBuildingId + "')  return n");
            ExecutionResult result = engine.execute(query);
            Iterator<Node> n_column = result.columnAs("n");
            Node building = n_column.next();
            //Removing the node from the index
            this.buildingsIndex.remove(building);
            for (Relationship rel : building.getRelationships()) {
                rel.delete();
            }
            //Deleting the node from the graph
            building.delete();

            tx.success();
        } finally {
            tx.finish();

        }
    }

    @Override
    public void clear() {
        this.graphDb.shutdown();
    }
}
