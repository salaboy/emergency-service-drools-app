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
    private List<String> entries = new ArrayList<String>(100);

    public Report() {
    }
    
    
    public void addEntry(String entry){
        entries.add(entry);
    }
    
    public String getReportString(){
        String result = "";
        for(String entry : entries){
            result += "     - " + entry +"\n";
        }
        return result;
    }
}
