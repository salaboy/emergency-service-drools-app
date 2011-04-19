/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.events.NotifierEvent;

/**
 *
 * @author salaboy
 */
public class EmergencyReachedNotifierEvent implements NotifierEvent{

    private Long ambulanceId;

    public EmergencyReachedNotifierEvent(Long ambulanceId) {
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
    
    

}
