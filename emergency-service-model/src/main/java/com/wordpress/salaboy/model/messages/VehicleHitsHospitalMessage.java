/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.buildings.Hospital;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class VehicleHitsHospitalMessage implements Serializable, EmergencyInterchangeMessage {
    private String vehicleId;
    private String callId;
    private Date time;
    private Hospital hospital;
    
    
    public VehicleHitsHospitalMessage(String vehicleId, Hospital hospital, String callId, Date time) {
        this.vehicleId = vehicleId;
        this.callId = callId;
        this.time = time;
        this.hospital = hospital;
    }

    @Override
    public String getEmergencyId() {
        return callId;
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

    public Hospital getHospital() {
        return hospital;
    }

    @Override
    public String toString() {
        return "VehicleHitsHospitalMessage{" + "vehicleId=" + vehicleId + ", callId=" + callId + ", time=" + time + ", hospital=" + hospital + '}';
    }
    
}
