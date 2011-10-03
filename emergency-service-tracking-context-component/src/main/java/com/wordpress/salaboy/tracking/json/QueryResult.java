package com.wordpress.salaboy.tracking.json;

/**
 * This response should be used for response of cypher queries.
 * @author calcacuervo
 *
 */
public class QueryResult {

	private String[][] data;
	
	private String[] columns;
	public QueryResult() {
	}
	public void setData(String[][] data) {
		this.data = data;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public String[] getColumns() {
		return columns;
	}
	public String[][] getData() {
		return data;
	}
}
