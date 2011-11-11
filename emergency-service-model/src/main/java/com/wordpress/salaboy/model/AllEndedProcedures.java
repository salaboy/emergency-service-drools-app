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
 * 
 * @TODO: test if we can delete this and just use the AllProceduresEndedEvent class
 * this class looks useless
 */
public class AllEndedProcedures implements Serializable{
    
    private List<String> selectedProcedures = new ArrayList<String>();
    private Long emergencyId;
    public AllEndedProcedures(Long emergencyId) {
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
    
    public int size(){
        return selectedProcedures.size();
    }
    
    public String get(int i){
        return selectedProcedures.get(i);
    }
}
