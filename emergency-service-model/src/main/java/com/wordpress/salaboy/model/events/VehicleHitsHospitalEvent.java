/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.events;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author salaboy
 */
public class VehicleHitsHospitalEvent implements EmergencyEvent, Serializable {

    private String emergencyId;
    private String vehicleId;
    private String hospitalId;
    private Date time;

    public VehicleHitsHospitalEvent(String emergencyid, String vehicleId, String hospitalId, Date date) {
        this.emergencyId = emergencyid;
        this.vehicleId = vehicleId;
        this.hospitalId = hospitalId;
        this.time = date;
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public Date getTime() {
        return time;
    }

    public String getVehicleId() {
        return vehicleId;
    }
}
