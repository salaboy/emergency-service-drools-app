/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

/**
 *
 * @author salaboy
 */
public class HospitalReachedNotifierEvent implements NotifierEvent{

    private Long ambulanceId;
    private Long hospitalId;

    public HospitalReachedNotifierEvent(Long ambulanceId, Long hospitalId) {
        this.ambulanceId = ambulanceId;
        this.hospitalId = hospitalId;
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

    public Long getHospitalId() {
        return hospitalId;
    }

    
}
