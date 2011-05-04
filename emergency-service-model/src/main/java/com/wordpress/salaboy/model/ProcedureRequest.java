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
    private Map<String, Object> parameters;

    public ProcedureRequest(String procedureName, Map<String, Object> parameters) {
        this.procedureName = procedureName;
        this.parameters = parameters;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getProcedure() {
        return procedurePrefix+procedureName;
    }
    
    
    
    
}
