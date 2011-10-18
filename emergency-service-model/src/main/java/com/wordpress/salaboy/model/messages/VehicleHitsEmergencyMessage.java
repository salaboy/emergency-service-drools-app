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
    private String emergencyId;
    private Date time;
    
    public VehicleHitsEmergencyMessage(String vehicleId, String emergencyId, Date time) {
        this.vehicleId = vehicleId;
        this.emergencyId = emergencyId;
        this.time = time; 
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergecyId) {
        this.emergencyId = emergecyId;
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
        return "VehicleHitsEmergencyMessage{" + "vehicleId=" + vehicleId + ", emergencyId=" + emergencyId + ", time=" + time + '}';
    }
    
    
    
}
