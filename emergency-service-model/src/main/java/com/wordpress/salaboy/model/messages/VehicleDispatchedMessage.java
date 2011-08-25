/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class VehicleDispatchedMessage implements Serializable ,EmergencyInterchangeMessage{
    private String callId;
    private String vehicleId;

    public VehicleDispatchedMessage(String callId, String vehicleId) {
        this.callId = callId;
        this.vehicleId = vehicleId;
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
        return "VehicleDispatchedMessage{" + "callId=" + callId + ", vehicleId=" + vehicleId + '}';
    }
    
    
     
}
