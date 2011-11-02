/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class AsyncProcedureStartMessage implements EmergencyInterchangeMessage, Serializable {
    private String emergencyId;
    private long workItemId;
    private String procedureName;
    private Map<String, Object> parameters;
    public AsyncProcedureStartMessage(String emergencyId, long workItemId, String procedureName, Map<String, Object> parameters) {
        this.emergencyId = emergencyId;
        this.workItemId = workItemId;
        this.procedureName = procedureName;
        this.parameters = parameters;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }
    
    
    
    
    
}
