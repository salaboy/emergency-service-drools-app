package com.wordpress.salaboy.tracking.json;

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

	public String getType() {
		return type;
	}
}