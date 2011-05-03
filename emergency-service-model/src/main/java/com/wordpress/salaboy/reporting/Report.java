/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.reporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class Report implements Serializable{
    private List<String> entries = new ArrayList<String>();

    public Report() {
    }
    
    
    public void addEntry(String entry){
        entries.add(entry);
    }

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }
    
    
    
    public String getReportString(){
        String result = "";
        for(String entry : entries){
            result += "     - " + entry +"\n";
        }
        return result;
    }

    @Override
    public String toString() {
        return "Report{" + "entries=" + entries + '}';
    }
    
    
}
