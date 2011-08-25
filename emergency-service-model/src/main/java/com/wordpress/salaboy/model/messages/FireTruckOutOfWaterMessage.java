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
public class FireTruckOutOfWaterMessage implements Serializable ,EmergencyInterchangeMessage{
    private String callId;
    private String vehicleId;
    private Date time;

    public FireTruckOutOfWaterMessage(String callId, String vehicleId, Date time) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.time = time;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "FireTruckOutOfWaterMessage{" + "callId=" + callId + ", vehicleId=" + vehicleId + ", time=" + time + '}';
    }
    
    
     
}
