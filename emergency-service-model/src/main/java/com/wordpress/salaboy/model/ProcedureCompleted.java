/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;

/**
 * Fact class that indicates that a Procedure was completed
 * @author esteban
 */
public class ProcedureCompleted implements Serializable {
    private String emergencyId;
    private String procedureId;

    public ProcedureCompleted(String emergencyId, String procedureId) {
        this.emergencyId = emergencyId;
        this.procedureId = procedureId;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }
    
}
