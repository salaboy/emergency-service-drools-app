/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.model.events;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class EmergencyEndsEvent implements CallEvent, Serializable {
    private String callId;
    
    private Date time;

    public EmergencyEndsEvent() {
    }

    public EmergencyEndsEvent(Date time) {
        this.time = time;
    }
    
    public EmergencyEndsEvent(String callId, Date time) {
        this.callId = callId;
        this.time = time;
    }

    @Override
    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
