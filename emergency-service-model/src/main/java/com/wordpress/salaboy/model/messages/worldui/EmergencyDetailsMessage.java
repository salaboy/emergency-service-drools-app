/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages.worldui;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class EmergencyDetailsMessage implements Serializable {
    private EmergencyType type;
    private int numberOfPeople;
    private long callId;

    /**
     * Constructs a <code>EmergencyDetailsMessage</code> from an instance
     * of <code>Emergency</code> class
     */
    public EmergencyDetailsMessage(Emergency emergency) {
        this.type = emergency.getType();
        this.numberOfPeople = emergency.getNroOfPeople(); 
        this.callId = emergency.getCall().getId();
    }
    
    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public EmergencyType getType() {
        return type;
    }

    public void setType(EmergencyType type) {
        this.type = type;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }
    
}
