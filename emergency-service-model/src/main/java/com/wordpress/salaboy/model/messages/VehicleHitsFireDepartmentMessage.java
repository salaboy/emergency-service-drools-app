/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.buildings.FirefightersDepartment;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class VehicleHitsFireDepartmentMessage implements Serializable, EmergencyInterchangeMessage {
    private String vehicleId;
    private String callId;
    private String emergencyId;
    private Date time;
    private FirefightersDepartment firefightersDepartment;
    
    public VehicleHitsFireDepartmentMessage(String vehicleId, FirefightersDepartment firefightersDepartment, String callId, String emergencyId, Date time) {
        this.vehicleId = vehicleId;
        this.callId = callId;
        this.emergencyId = emergencyId;
        this.time = time;
        this.firefightersDepartment = firefightersDepartment;
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
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

    public FirefightersDepartment getFirefightersDepartment() {
        return firefightersDepartment;
    }

    @Override
    public String toString() {
        return "VehicleHitsFireDepartmentMessage{" + "vehicleId=" + vehicleId + ", callId=" + callId + ", time=" + time + ", firefightersDepartment=" + firefightersDepartment + '}';
    }

}
