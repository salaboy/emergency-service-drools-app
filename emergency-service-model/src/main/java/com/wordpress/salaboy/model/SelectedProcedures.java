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
public class SelectedProcedures implements Serializable{
    
    private List<String> selectedProcedures = new ArrayList<String>();
    private Long emergencyId;
    public SelectedProcedures(Long emergencyId) {
        this.emergencyId = emergencyId;
    }

    public Long getEmergencyId() {
        return emergencyId;
    }
    
    
    public void addSelectedProcedureName(String procedureName){
        selectedProcedures.add(procedureName);
    }
    
    public String getSelectedProceduresString(){
        String result = "[";
        for(String value : selectedProcedures){
            result += value + ": ";
        }
        result +="]";
        return result;
    }
    
}
