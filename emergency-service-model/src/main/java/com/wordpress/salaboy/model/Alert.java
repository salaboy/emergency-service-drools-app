package com.wordpress.salaboy.model;

import java.util.Date;

public class Alert {

	public enum AlertType{NORMAL, WARNING};

	private AlertType type;
	private String message;
	private Date time;

	public Alert() {}

	public Alert(AlertType type, String message) {
		this.type = type;
		this.message = message;
		this.time = new Date();
	}

	public void setType(AlertType type) {
		this.type = type;
	}

	public AlertType getType() {
		return type;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getTime() {
		return time;
	}

}