/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author esteban
 */
public class Logger {
    private List<String> logs = new ArrayList<String>();
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
    
    public synchronized void addMessage(String message){
        logs.add(dateFormat.format(new Date(System.currentTimeMillis()))+" - "+message);
    }
    
    public synchronized void clearMessages(){
        logs.clear();
    }
    
    public List<String> getLogs(){
        return Collections.unmodifiableList(this.logs);
    }
    
}
