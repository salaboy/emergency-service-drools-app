/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.Hospital;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class VehicleHitsHospitalMessage implements Serializable {
    private Long vehicleId;
    private Long callId;
    private Date time;
    private Hospital hospital;
    public VehicleHitsHospitalMessage(Long vehicleId, Hospital hospital, Long callId, Date time) {
        this.vehicleId = vehicleId;
        this.callId = callId;
        this.time = time;
        this.hospital = hospital;
    }

    

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long emergecyId) {
        this.callId = emergecyId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
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
    
}
