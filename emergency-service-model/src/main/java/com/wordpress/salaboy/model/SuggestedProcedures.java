/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class SuggestedProcedures implements Serializable{
	
	
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private List<String> procedures = new ArrayList<String>();
    
    private Long emergencyId;
    public SuggestedProcedures(Long emergencyId) {
        this.emergencyId = emergencyId;
    }

    public Long getEmergencyId() {
        return emergencyId;
    }
    
    public void addProcedureName(String procedureName){
        procedures.add(procedureName);
    }
    
    public String getSuggestedProceduresString(){
        String result = "[";
        for(String value : procedures){
            result += value + ": ";
        }
        result +="]";
        return result;
    } 
}
