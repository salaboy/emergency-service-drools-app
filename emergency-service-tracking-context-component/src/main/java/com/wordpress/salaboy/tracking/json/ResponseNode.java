package com.wordpress.salaboy.tracking.json;

import java.util.List;
import java.util.Map;

/**
 * Neo4j api responses has general structure of response for getting information
 * about node. This class will be used to unmarshall json response.
 * 
 * @author calcacuervo
 * 
 */
public class ResponseNode {

	private String self;

	private String end;

	private String type;

	private Map<String, String> data;

	public ResponseNode() {
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getEnd() {
		return end;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getData() {
		return data;
	}

	public String getType() {
		return type;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
}