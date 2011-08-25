/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class VehicleHitsEmergencyMessage implements Serializable, EmergencyInterchangeMessage {
    private String vehicleId;
    private String callId;
    private Date time;
    
    public VehicleHitsEmergencyMessage(String vehicleId, String callId, Date time) {
        this.vehicleId = vehicleId;
        this.callId = callId;
        this.time = time; 
    }

    @Override
    public String getCallId() {
        return callId;
    }

    public void setCallId(String emergecyId) {
        this.callId = emergecyId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "VehicleHitsEmergencyMessage{" + "vehicleId=" + vehicleId + ", callId=" + callId + ", time=" + time + '}';
    }
    
    
    
}
