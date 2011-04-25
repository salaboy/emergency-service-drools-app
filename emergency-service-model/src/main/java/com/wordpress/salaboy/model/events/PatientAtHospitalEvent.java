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
public class PatientAtHospitalEvent implements Serializable{
    private Long callId;
    private Long vehicleId;
    private Long hospitalId;
    private Date time;
    
    public PatientAtHospitalEvent(Long callId, Long vehicleId, Long hospitalId, Date date) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.hospitalId = hospitalId;
        this.time = date;
    }

    

    public Long getCallId() {
        return callId;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public Date getTime() {
        return time;
    }

    public Long getVehicleId() {
        return vehicleId;
    }
    
    
    

}
