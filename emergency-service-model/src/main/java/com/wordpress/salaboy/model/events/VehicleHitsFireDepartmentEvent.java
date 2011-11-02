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
public class VehicleHitsFireDepartmentEvent implements EmergencyVehicleEvent, Serializable {

    private String emergencyId;
    private String vehicleId;
    private String fireDepartmentId;
    private Date time;

    public VehicleHitsFireDepartmentEvent(String emergencyid, String vehicleId, String fireDepartmentId, Date date) {
        this.emergencyId = emergencyid;
        this.vehicleId = vehicleId;
        this.fireDepartmentId = fireDepartmentId;
        this.time = date;
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getFireDepartmentId() {
        return fireDepartmentId;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String getVehicleId() {
        return vehicleId;
    }

    @Override
    public String toString() {
        return "VehicleHitsHospitalEvent{" + "emergencyId=" + emergencyId + ", vehicleId=" + vehicleId + ", fireDepartmentId=" + fireDepartmentId + ", time=" + time + '}';
    }
}
