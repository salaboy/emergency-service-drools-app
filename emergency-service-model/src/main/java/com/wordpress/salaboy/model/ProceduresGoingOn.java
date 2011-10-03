/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.List;

/**
 * Fact class that indicates that one or more procedures are going on as part of 
 * an emergency
 * @author esteban
 */
public class ProceduresGoingOn implements Serializable {
    private String emergencyId;
    private List<String> procedureIds;

    public ProceduresGoingOn(String emergencyId, List<String> procedureIds) {
        this.emergencyId = emergencyId;
        this.procedureIds = procedureIds;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public List<String> getProcedureIds() {
        return procedureIds;
    }

    public void setProcedureIds(List<String> procedureIds) {
        this.procedureIds = procedureIds;
    }

    public int size(){
        return this.procedureIds.size();
    }
    
    
    
}
