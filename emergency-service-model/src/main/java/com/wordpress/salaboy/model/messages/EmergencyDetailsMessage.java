/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class EmergencyDetailsMessage implements Serializable {
    private Emergency emergency;
    

    /**
     * Constructs a <code>EmergencyDetailsMessage</code> from an instance
     * of <code>Emergency</code> class
     */
    public EmergencyDetailsMessage(Emergency emergency) {
        this.emergency = emergency;
    }
    
    public int getNumberOfPeople() {
        return emergency.getNroOfPeople();
    }

   

    public EmergencyType getType() {
        return emergency.getType();
    }

   

    public long getCallId() {
        return emergency.getCall().getId();
    }

    public Emergency getEmergency() {
        return emergency;
    }

   
    
}
