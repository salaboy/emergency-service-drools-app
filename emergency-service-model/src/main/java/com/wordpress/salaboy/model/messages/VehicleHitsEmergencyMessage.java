/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.Hospital;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class VehicleHitsEmergencyMessage implements Serializable, EmergencyInterchangeMessage {
    private Long vehicleId;
    private String callId;
    private Date time;
    
    public VehicleHitsEmergencyMessage(Long vehicleId, String callId, Date time) {
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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
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
