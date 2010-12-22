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

    private WiiMoteEvent heartBeatEvent;
    private Long ambulanceId;

    public PatientVitalSignNotifierEvent(WiiMoteEvent heartBeatEvent, Long ambulanceId) {
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

    public WiiMoteEvent getHeartBeatEvent() {
        return heartBeatEvent;
    }
    
    

}
