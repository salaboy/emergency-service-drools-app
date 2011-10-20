package com.wordpress.salaboy.context.tracking;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.GraphDatabaseService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wordpress.salaboy.context.tracking.json.QueryResult;
import com.wordpress.salaboy.context.tracking.json.ResponseNode;

/**
 * Implementation of {@link ContextTrackingService} which used Rest API of
 * Neo4j.
 *
 * @author calcacuervo
 */
public class ContextTrackingServiceRest implements ContextTrackingService {

    public GraphDatabaseService getGraphDb() {
        return null;
    }
    private static Log LOG = LogFactory.getLog(ContextTrackingServiceRest.class);
    private HttpClient httpClient;
    private String baseUri;
    private static ContextTrackingServiceRest instance;
    private String contentType = "application/json";
    private String createNodeUrlPath = "/db/data/node";
    private String createIndexUrlPath = "/db/data/index/node/";
    private String associateNodeToIndexUrlPath = "/db/data/index/node/";

    public static ContextTrackingServiceRest getInstance() {
        if (instance == null) {
            instance = new ContextTrackingServiceRest("http://0.0.0.0:7575");
        }
        return instance;
    }

    public ContextTrackingServiceRest(String baseUri) {
        this.httpClient = new HttpClient();
        this.baseUri = baseUri;
        this.createIndexes();
    }

    @Override
    public String newServiceChannelId() {

        String channelId = "Channel-" + UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("channelId", channelId);
        params.put("name", channelId);
        this.createNewNode(params);
        this.associateNodeToIndex(this.createNewNode(params), "channels",
                params);

        return channelId;

    }

    @Override
    public String newProcedureId() {
        String procedureId = "Procedure-" + UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("procedureId", procedureId);
        params.put("name", procedureId);
        this.associateNodeToIndex(this.createNewNode(params), "procedures",
                params);
        return procedureId;

    }

    @Override
    public String newCallId() {
        String callId = "Call-" + UUID.randomUUID().toString();

        Map<String, String> params = new HashMap<String, String>();
        params.put("callId", callId);
        params.put("name", callId);
        params.put("phoneNumber", "555-1234");
        this.associateNodeToIndex(this.createNewNode(params), "calls", params);

        return callId;

    }

    @Override
    public String newVehicleId() {
        String vehicleId = "Vehicle-" + UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("vehicleId", vehicleId);
        params.put("name", vehicleId);

        this.associateNodeToIndex(this.createNewNode(params), "vehicles",
                params);

        return vehicleId;

    }

    @Override
    public String newEmergencyId() {
        String emergencyId = "Emergency-" + UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("emergencyId", emergencyId);
        params.put("name", emergencyId);
        this.associateNodeToIndex(this.createNewNode(params), "emergencies",
                params);

        return emergencyId;
    }

    @Override
    public String newEmergencyEntityBuildingId() {
        String buildingId = "EntityBuilding-" + UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("buildingId", buildingId);
        params.put("name", buildingId);
        this.associateNodeToIndex(this.createNewNode(params), "buildings",
                params);

        return buildingId;
    }

    @Override
    public String newPatientId() {
        String patientId = "Patient-" + UUID.randomUUID().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("patientId", patientId);
        params.put("name", patientId);
        this.associateNodeToIndex(this.createNewNode(params), "patients",
                params);

        return patientId;
    }

    private String createRelationship(String nodeFrom, String nodeTo,
            String type) {
        try {
            String relationshipUrl = nodeFrom + "/relationships";
            PostMethod method = new PostMethod(relationshipUrl);
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            String body = "{ \"to\": \"" + nodeTo + "\",\"type\":\"" + type
                    + "\"}";
            method.setRequestEntity(new StringRequestEntity(body,
                    this.contentType, "UTF-8"));
            this.httpClient.executeMethod(method);
            Gson gson = new Gson();
            ResponseNode node = gson.fromJson(method.getResponseBodyAsString(),
                    ResponseNode.class);
            return node.getSelf();
        } catch (Exception e) {
            throw new RuntimeException(
                    "There was an error creating relationship." + nodeFrom
                    + " " + nodeTo, e);
        }
    }

    @Override
    public void attachEmergency(String callId, String emergencyId) {
        try {
            GetMethod method = new GetMethod(this.baseUri
                    + this.createIndexUrlPath + "calls/callId/" + callId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            Gson gson = new Gson();
            Collection<ResponseNode> node = gson.fromJson(
                    method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String callnode = node.iterator().next().getSelf();

            method = new GetMethod(this.baseUri + this.createIndexUrlPath
                    + "emergencies/emergencyId/" + emergencyId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            gson = new Gson();

            node = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String emergencyNode = node.iterator().next().getSelf();
            this.createRelationship(callnode, emergencyNode,
                    EmergencyRelationshipType.CREATES.name());

        } catch (Exception e) {
            throw new RuntimeException(
                    "There was an error attaching the emergency to the call", e);
        }

    }

    @Override
    public void attachProcedure(String emergencyId, String procedureId) {
        try {
            GetMethod method = new GetMethod(this.baseUri
                    + this.createIndexUrlPath + "emergencies/emergencyId/"
                    + emergencyId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            Gson gson = new Gson();
            Collection<ResponseNode> node = gson.fromJson(
                    method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String emergencynode = node.iterator().next().getSelf();

            method = new GetMethod(this.baseUri + this.createIndexUrlPath
                    + "procedures/procedureId/" + procedureId);
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
            gson = new Gson();
            node = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String callNode = node.iterator().next().getSelf();
            this.createRelationship(emergencynode, callNode,
                    EmergencyRelationshipType.INSTANTIATE.name());
        } catch (Exception e) {
            throw new RuntimeException(
                    "There was an error attaching procedure", e);
        }

    }

    @Override
    public void attachProcedures(String parentProcedureId,
            String childProcedureId) {
        try {
            GetMethod method = new GetMethod(this.baseUri
                    + this.createIndexUrlPath + "procedures/procedureId/"
                    + parentProcedureId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            Gson gson = new Gson();
            Collection<ResponseNode> node = gson.fromJson(
                    method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());

            String parentNode = node.iterator().next().getSelf();

            method = new GetMethod(this.baseUri + this.createIndexUrlPath
                    + "procedures/procedureId/" + childProcedureId);
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
            gson = new Gson();
            node = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String childNode = node.iterator().next().getSelf();
            this.createRelationship(parentNode, childNode,
                    EmergencyRelationshipType.SUB.name());
        } catch (Exception e) {
            throw new RuntimeException(
                    "There was an error attaching procedure", e);
        }
    }

    @Override
    public void attachVehicle(String procedureId, String vehicleId) {
        try {
            GetMethod method = new GetMethod(this.baseUri
                    + this.createIndexUrlPath + "procedures/procedureId/"
                    + procedureId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            Gson gson = new Gson();
            Collection<ResponseNode> node = gson.fromJson(
                    method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String procedureNode = node.iterator().next().getSelf();

            method = new GetMethod(this.baseUri + this.createIndexUrlPath
                    + "vehicles/vehicleId/" + vehicleId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            gson = new Gson();
            node = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String vehicleNode = node.iterator().next().getSelf();
            this.createRelationship(procedureNode, vehicleNode,
                    EmergencyRelationshipType.USE.name());
        } catch (Exception e) {
            throw new RuntimeException(
                    "There was a problem attaching vehicle", e);
        }
    }

    @Override
    public void attachServiceChannel(String emergencyId, String channelId) {
        try {
            GetMethod method = new GetMethod(this.baseUri
                    + this.createIndexUrlPath + "emergencies/emergencyId/"
                    + emergencyId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            Gson gson = new Gson();
            Collection<ResponseNode> node = gson.fromJson(
                    method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String emergencynode = node.iterator().next().getSelf();

            method = new GetMethod(this.baseUri + this.createIndexUrlPath
                    + "channels/channelId/" + channelId);
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            gson = new Gson();
            node = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<Collection<ResponseNode>>() {
                    }.getType());
            String callNode = node.iterator().next().getSelf();
            this.createRelationship(emergencynode, callNode,
                    EmergencyRelationshipType.CONSUME.name());
        } catch (Exception e) {
            throw new RuntimeException(
                    "There was an error attaching channel", e);
        }
    }

    //*******************Util methods*********************
    private void associateNodeToIndex(String nodeUrl, String indexName,
            Map<String, String> props) {
        for (String key : props.keySet()) {
            PostMethod method = new PostMethod(this.baseUri
                    + this.associateNodeToIndexUrlPath + indexName + "/" + key
                    + "/" + props.get(key));
            try {
                method.setRequestEntity(new StringRequestEntity("\"" + nodeUrl
                        + "\"", this.contentType, "UTF-8"));
                method.addRequestHeader("Accept", this.contentType);
                method.addRequestHeader("Content-Type", this.contentType);
                this.httpClient.executeMethod(method);
                if (method.getStatusCode() > 300) {
                    LOG.error("Error creating indexes..."
                            + method.getResponseBodyAsString());
                    throw new RuntimeException(
                            "There was an error associating node to index."
                            + nodeUrl + " " + indexName);

                }
            } catch (Exception e) {
                throw new RuntimeException(
                        "There was an error associating node to index."
                        + nodeUrl + " " + indexName, e);
            }
        }
    }

    private void createIndexes() {
        try {
            PostMethod method = new PostMethod(this.baseUri
                    + this.createIndexUrlPath);
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "emergencies" + "\"}", this.contentType, "UTF-8"));
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
            LOG.debug(method.getResponseBodyAsString());

            method = new PostMethod(this.baseUri + this.createIndexUrlPath);
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "calls" + "\"}", this.contentType, "UTF-8"));
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
            LOG.debug(method.getResponseBodyAsString());

            method = new PostMethod(this.baseUri + this.createIndexUrlPath);
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "procedures" + "\"}", this.contentType, "UTF-8"));
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
            LOG.debug(method.getResponseBodyAsString());

            method = new PostMethod(this.baseUri + this.createIndexUrlPath);
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "emergencies" + "\"}", this.contentType, "UTF-8"));
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "vehicles" + "\"}", this.contentType, "UTF-8"));
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
            LOG.debug(method.getResponseBodyAsString());

            method = new PostMethod(this.baseUri + this.createIndexUrlPath);
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "channels" + "\"}", this.contentType, "UTF-8"));
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);

            method = new PostMethod(this.baseUri + this.createIndexUrlPath);
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "buildings" + "\"}", this.contentType, "UTF-8"));
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
            LOG.debug(method.getResponseBodyAsString());

            method = new PostMethod(this.baseUri + this.createIndexUrlPath);
            method.setRequestEntity(new StringRequestEntity("{\"name\": \""
                    + "patients" + "\"}", this.contentType, "UTF-8"));
            method.addRequestHeader("Accept", this.contentType);
            method.addRequestHeader("Content-Type", this.contentType);
            this.httpClient.executeMethod(method);
        } catch (Exception e) {
            throw new RuntimeException("There was an error creating indexes", e);
        }
    }

    private String createNewNode(Map<String, String> properties) {
        PostMethod method = new PostMethod(this.baseUri
                + this.createNodeUrlPath);

        for (String key : properties.keySet()) {
            method.addParameter(key, properties.get(key));
        }
        try {
            method.addRequestHeader("Accept", this.contentType);
            this.httpClient.executeMethod(method);
            return method.getResponseHeader("Location").getValue();
        } catch (Exception e) {
            throw new RuntimeException("There was an error creating indexes", e);
        }
    }

    @Override
    public String getProcedureAttachedToVehicle(String vehicleId) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(this.baseUri + "/db/data/ext/CypherPlugin/graphdb/execute_query");
            method.setRequestHeader("Content-type", "application/json");
            method.setRequestHeader("Accept", "application/json");
            String content = "{\"query\": \"start v=(vehicles, 'vehicleId:" + vehicleId + "')  match (v) <-[USE]- (w)    return w\"}";
            method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
            client.executeMethod(method);

            Gson gson = new Gson();

            QueryResult result = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<QueryResult>() {
                    }.getType());
            //TODO CHECK THIS WITH A TEST!
            return result.getData().get(0).get(0).getData().get("procedureId");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
    }

    public void detachVehicle(String vehicleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void detachProcedure(String procedureId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void detachEmergency(String emergencyId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void detachEmergencyEntityBuilding(String entityBuildingId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void detachPatient(String patientId) {
        // TODO Auto-generated method stub
    }

    @Override
    public void detachServiceChannel(String serviceChannelId) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getCallAttachedToEmergency(String emergencyId) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(this.baseUri + "/db/data/ext/CypherPlugin/graphdb/execute_query");
            method.setRequestHeader("Content-type", "application/json");
            method.setRequestHeader("Accept", "application/json");
            String content = "{\"query\": \"start e=(emergencies, 'emergencyId:" + emergencyId + "')  match (c) -[CREATES]-> (e)    return c\"}";
            method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
            client.executeMethod(method);

            Gson gson = new Gson();

            QueryResult result = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<QueryResult>() {
                    }.getType());
            //TODO CHECK THIS WITH A TEST!
            
            if (result.getData().isEmpty()){
                //No results
                return null;
            }
            
            return result.getData().get(0).get(0).getData().get("callId");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getEmergencyAttachedToCall(String callId) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(this.baseUri + "/db/data/ext/CypherPlugin/graphdb/execute_query");
            method.setRequestHeader("Content-type", "application/json");
            method.setRequestHeader("Accept", "application/json");
            String content = "{\"query\": \"start c=(calls, 'callId:" + callId + "')  match (c) -[CREATES]-> (e)    return e\"}";
            method.setRequestEntity(new StringRequestEntity(content, "application/json", "UTF-8"));
            client.executeMethod(method);

            Gson gson = new Gson();

            QueryResult result = gson.fromJson(method.getResponseBodyAsString(),
                    new TypeToken<QueryResult>() {
                    }.getType());
            //TODO CHECK THIS WITH A TEST!
            
            if (result.getData().isEmpty()){
                //No results
                return null;
            }
            return result.getData().get(0).get(0).getData().get("emergencyId");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
