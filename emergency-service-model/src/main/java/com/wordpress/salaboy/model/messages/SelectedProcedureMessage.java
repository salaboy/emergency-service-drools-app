/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class SelectedProcedureMessage implements Serializable, EmergencyInterchangeMessage{
    private String procedureName;
    private String callId;
    private Map<String, Object> parameters;

    public SelectedProcedureMessage(String procedureName, String callId, Map<String, Object> parameters) {
        this.procedureName = procedureName;
        this.callId = callId;
        this.parameters = parameters;
    }

    @Override
    public String getEmergencyId() {
        return callId;
    }


    public String getProcedureName() {
        return procedureName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void addParameter(String key, Object value){
        if(parameters == null){
            parameters = new HashMap<String, Object>();
        }
        parameters.put(key, value);
    }
    
    public Object getParameter(String key){
        return parameters.get(key);
    }

    @Override
    public String toString() {
        return "SelectedProcedureMessage{" + "procedureName=" + procedureName + ", callId=" + callId + ", parameters=" + parameters + '}';
    }
    
    
    
    
}
