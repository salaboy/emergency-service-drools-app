/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.model.events;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class VehicleHitsEmergencyEvent implements EmergencyVehicleEvent, Serializable {
    private String emergencyId;
    private String vehicleId;
    
    private Date time;

    public VehicleHitsEmergencyEvent() {
    }

    public VehicleHitsEmergencyEvent(Date time) {
        this.time = time;
    }
    
    public VehicleHitsEmergencyEvent(String emergencyid, String vehicleId, Date time) {
        this.emergencyId = emergencyid;
        this.vehicleId = vehicleId;
        this.time = time;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "VehicleHitsEmergencyEvent{" + "emergencyId=" + emergencyId + ", vehicleId=" + vehicleId + ", time=" + time + '}';
    }

}
