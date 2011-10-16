/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.FirefightersDepartment;
import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class FirefightersDepartmentSelectedMessage implements Serializable, EmergencyInterchangeMessage{
    private FirefightersDepartment firefightersDepartment;
    private String callId;

    public FirefightersDepartmentSelectedMessage(String callId, FirefightersDepartment firefigthersDepartment) {
        this.firefightersDepartment = firefigthersDepartment;
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public FirefightersDepartment getFirefightersDepartment() {
        return firefightersDepartment;
    }

    public void setFirefightersDepartment(FirefightersDepartment firefigthersDepartment) {
        this.firefightersDepartment = firefigthersDepartment;
    }

    @Override
    public String toString() {
        return "FireFighterDepartmentSelectedMessage{" + "firefigthersDepartment=" + firefightersDepartment + ", callId=" + callId + '}';
    }

    @Override
    public String getEmergencyId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
    
}