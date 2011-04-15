/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

/**
 *
 * @author salaboy
 */
public class HospitalSelectedNotifierEvent implements NotifierEvent{

    private Long ambulanceId;
    private Long hospitalId;
    private Long emergencyId;

    public HospitalSelectedNotifierEvent(Long ambulanceId, Long hospitalId, Long emergencyId) {
        this.ambulanceId = ambulanceId;
        this.hospitalId = hospitalId;
        this.emergencyId = emergencyId;
    }
    
    
    @Override
    public String getEventType() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public Long getEventId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Long getAmbulanceId() {
        return ambulanceId;
    }

    public Long getEmergencyId() {
        return emergencyId;
    }

    public Long getHospitalId() {
        return hospitalId;
    }
    
    

}
