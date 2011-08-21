/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class EmergencyEndsMessage implements Serializable, EmergencyInterchangeMessage {
    private Long callId;
    private Date time;
    
    public EmergencyEndsMessage(Long callId, Date time) {
        this.callId = callId;
        this.time = time;
    }

    @Override
    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "EmergencyEndsMessage{" + "call id=" + callId + ", time=" + time + '}';
    }

    
  

   
    
}
