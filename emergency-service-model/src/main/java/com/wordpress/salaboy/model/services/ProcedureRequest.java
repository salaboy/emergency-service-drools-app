/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.services;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class ProcedureRequest implements Serializable {
    private String emergencyId;
    private String procedureName;
    private Map<String, Object> parameters;

    public ProcedureRequest(String emergencyId, String procedureName, Map<String, Object> parameters) {
        this.emergencyId = emergencyId;
        this.procedureName = procedureName;
        this.parameters = parameters;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getProcedureName() {
        return procedureName;
    }
    
    
}
