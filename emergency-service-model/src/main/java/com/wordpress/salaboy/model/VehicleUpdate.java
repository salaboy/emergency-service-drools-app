package com.wordpress.salaboy.model;

import java.io.Serializable;

public class VehicleUpdate implements Serializable {
	
	private String comment;
	
	private int priority;

	public VehicleUpdate(String comment, int priority) {
		super();
		this.comment = comment;
		this.priority = priority;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "Update. Priority: " + priority + " Comment " + comment;
	}
}
