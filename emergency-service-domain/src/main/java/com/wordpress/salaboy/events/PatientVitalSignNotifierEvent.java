/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;


/**
 *
 * @author salaboy
 */
public class PatientVitalSignNotifierEvent implements NotifierEvent {

    private PulseEvent heartBeatEvent;
    private Long ambulanceId;

    public PatientVitalSignNotifierEvent(PulseEvent heartBeatEvent, Long ambulanceId) {
        this.heartBeatEvent = heartBeatEvent;
        this.ambulanceId = ambulanceId;
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

    public PulseEvent getHeartBeatEvent() {
        return heartBeatEvent;
    }
    
    

}
