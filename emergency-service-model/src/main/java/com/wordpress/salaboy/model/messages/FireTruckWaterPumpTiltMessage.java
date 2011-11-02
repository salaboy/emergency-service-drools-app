/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class FireTruckWaterPumpTiltMessage implements Serializable ,EmergencyInterchangeMessage{

    private String emergencyId;
    private String vehicleId;
    private int value;

    public FireTruckWaterPumpTiltMessage(String emergencyId, String vehicleId, int value) {
        this.emergencyId = emergencyId;
        this.vehicleId = vehicleId;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

   
    public String getVehicleId() {
        return vehicleId;
    }

    
    @Override
    public String getEmergencyId() {
        return this.emergencyId;
    }

    @Override
    public String toString() {
        return "FireTruckWaterPumpTiltMessage{" + "emergencyId=" + emergencyId + ", vehicleId=" + vehicleId + ", value=" + value + '}';
    }
    
    
    
}
