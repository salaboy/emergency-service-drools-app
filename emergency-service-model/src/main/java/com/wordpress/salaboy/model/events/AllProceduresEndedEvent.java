/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.events;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class AllProceduresEndedEvent implements Serializable{
    private String emergencyId;
    private List<String> endedProcedures;

    public AllProceduresEndedEvent(String emergencyId, List<String> endedProcedures) {
        this.emergencyId = emergencyId;
        this.endedProcedures = endedProcedures;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public List<String> getEndedProcedures() {
        return endedProcedures;
    }

    public void setEndedProcedures(List<String> endedProcedures) {
        this.endedProcedures = endedProcedures;
    }
    
    
}
