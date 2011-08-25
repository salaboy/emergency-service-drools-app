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
    private String emergencyId;
    public SelectedProcedures(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getEmergencyId() {
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
    
    public int size(){
        return selectedProcedures.size();
    }
    
    public String get(int i){
        return selectedProcedures.get(i);
    }

    public List<String> getSelectedProcedures() {
        return selectedProcedures;
    }
    
    
}
