/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class PulseEvent implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private boolean processed;
    private int value;

    public PulseEvent(int value) {
        this.value = value;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    
    public boolean isProcessed() {
        return processed;
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString(){
        return "[PulseEvent: "+" value = "+this.getValue()+", processed = "+this.isProcessed()+"]";
    }

}
