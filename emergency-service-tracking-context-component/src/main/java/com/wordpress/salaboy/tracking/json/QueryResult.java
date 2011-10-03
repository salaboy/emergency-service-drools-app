package com.wordpress.salaboy.tracking.json;

import java.util.List;

/**
 * This response should be used for response of cypher queries.
 * @author calcacuervo
 *
 */
public class QueryResult {

	private List<List<ResponseNode>> data;
	
	public QueryResult() {
	}
	
	public void setData(List<List<ResponseNode>> data) {
		this.data = data;
	}
	
	public List<List<ResponseNode>> getData() {
		return data;
	}
	
}
