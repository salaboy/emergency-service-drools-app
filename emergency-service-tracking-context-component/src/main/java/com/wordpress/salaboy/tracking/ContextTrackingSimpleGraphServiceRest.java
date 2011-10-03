package com.wordpress.salaboy.tracking;

import java.util.Collection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wordpress.salaboy.tracking.json.ResponseNode;

/**
 * 
 * @author calcacuervo
 *
 */
public class ContextTrackingSimpleGraphServiceRest implements
		ContextTrackingGraphService {

	private HttpClient httpClient;
	private String baseUri;
	private String createIndexUrlPath = "/db/data/index/node/";
	private String contentType = "application/json";

	public ContextTrackingSimpleGraphServiceRest(String baseUri) {
		this.httpClient = new HttpClient();
		this.baseUri = baseUri;
	}

	@Override
	public String graphEmergency(String emergencyId) {
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
			String emergencyNode = node.iterator().next().getSelf();

			String resultString = this.getNodeProperty(emergencyNode, "name")
					+ "/n";
			method = new GetMethod(emergencyNode + "/" + "relationships/out");
			this.httpClient.executeMethod(method);
			node = gson.fromJson(method.getResponseBodyAsString(),
					new TypeToken<Collection<ResponseNode>>() {
					}.getType());
			for (ResponseNode indexedNode : node) {
				resultString += this.graphRelationships(indexedNode.getEnd(),
						indexedNode.getType());
			}
			resultString += "--";

			return resultString;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String getNodeProperty(String node, String name) {
		try {
			GetMethod method = new GetMethod(node + "/properties/" + name);
			method.addRequestHeader("Accept", this.contentType);
			this.httpClient.executeMethod(method);
			return method.getResponseBodyAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String graphRelationships(String node, String type) {
		try {
			String resultString = "";
			resultString += type + "\n";
			resultString += this.getNodeProperty(node, "name") + "/n" + "\n";

			GetMethod method = new GetMethod(node + "/" + "relationships/out");
			this.httpClient.executeMethod(method);
			Gson gson = new Gson();
			Collection<ResponseNode> relationshipNode = gson.fromJson(
					method.getResponseBodyAsString(),
					new TypeToken<Collection<ResponseNode>>() {
					}.getType());
			for (ResponseNode indexedNode : relationshipNode) {
				resultString += this.graphRelationships(indexedNode.getEnd(),
						indexedNode.getType());
			}
			return resultString;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
