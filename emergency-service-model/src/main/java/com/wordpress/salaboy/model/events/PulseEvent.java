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
public class PulseEvent implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private boolean processed;
    private int value;
    private Long vehicleId;
    private Long callId;

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

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "PulseEvent{" + "processed=" + processed + ", value=" + value + ", vehicleId=" + vehicleId + ", callId=" + callId + '}';
    }

}
