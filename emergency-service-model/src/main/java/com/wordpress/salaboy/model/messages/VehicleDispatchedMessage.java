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
    private String emergencyId;
    private String vehicleId;

    public VehicleDispatchedMessage(String emergencyId, String vehicleId) {
        this.emergencyId = emergencyId;
        this.vehicleId = vehicleId;
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
    }

    
    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }
    
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "VehicleDispatchedMessage{" + "callId=" + emergencyId + ", vehicleId=" + vehicleId + '}';
    }
    
    
     
}
