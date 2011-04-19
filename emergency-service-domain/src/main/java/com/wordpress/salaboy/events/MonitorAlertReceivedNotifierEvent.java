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
public class MonitorAlertReceivedNotifierEvent implements NotifierEvent {

    private String message;
    private Long emergencyId;

    public MonitorAlertReceivedNotifierEvent(String message, Long emergencyId) {
        this.message = message;
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

    public Long getEmergencyId() {
        return emergencyId;
    }

    public String getMessage() {
        return message;
    }

    
    
}
