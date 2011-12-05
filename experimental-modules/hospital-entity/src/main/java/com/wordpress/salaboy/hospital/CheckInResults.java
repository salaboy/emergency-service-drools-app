/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class CheckInResults implements Serializable{
    private String id;
    private String gate;
    private long checkinTimestamp;
    private boolean notified;
    private boolean checkedIn;
    
    public CheckInResults(String id) {
        this.id = id;
    }

    public CheckInResults(String id, String gate, long checkinTimestamp) {
        this.id = id;
        this.gate = gate;
        this.checkinTimestamp = checkinTimestamp;
    }

    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCheckinTimestamp() {
        return checkinTimestamp;
    }

    public void setCheckinTimestamp(long checkinTimestamp) {
        this.checkinTimestamp = checkinTimestamp;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}

	public boolean isCheckedIn() {
		return checkedIn;
	}
    
    
    
}
