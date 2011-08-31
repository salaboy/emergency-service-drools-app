/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class ProcedureRequest implements Serializable{
    private final String procedurePrefix = "com.wordpress.salaboy.bpmn2.";
    private String procedureName;
    private String procedureId;
    private Map<String, Object> parameters;

    
    public ProcedureRequest(String procedureId, String procedureName, Map<String, Object> parameters) {
        this.procedureId = procedureId;
        this.procedureName = procedureName;
        this.parameters = parameters;
        parameters.put("procedureId", procedureId);
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getProcedure() {
        return procedurePrefix+procedureName;
    }

    public String getProcedureId() {
        return procedureId;
    }
    
    
    
    
    
    
}
