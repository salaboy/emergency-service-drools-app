/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.model.events;

import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class PulseEvent implements CallEvent, Serializable{
    private static final long serialVersionUID = 1L;
    
    private boolean processed;
    private int value;
    private String vehicleId;
    private String callId;

    public PulseEvent(int value) {
        this.value = value;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    
    public boolean isProcessed() {
        return processed;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "PulseEvent{" + "processed=" + processed + ", value=" + value + ", vehicleId=" + vehicleId + ", callId=" + callId + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PulseEvent other = (PulseEvent) obj;
        if (this.processed != other.processed) {
            return false;
        }
        if (this.value != other.value) {
            return false;
        }
        if (this.vehicleId != other.vehicleId && (this.vehicleId == null || !this.vehicleId.equals(other.vehicleId))) {
            return false;
        }
        if (this.callId != other.callId && (this.callId == null || !this.callId.equals(other.callId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.processed ? 1 : 0);
        hash = 31 * hash + this.value;
        hash = 31 * hash + (this.vehicleId != null ? this.vehicleId.hashCode() : 0);
        hash = 31 * hash + (this.callId != null ? this.callId.hashCode() : 0);
        return hash;
    }
    
    

}
