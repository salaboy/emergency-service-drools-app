/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import java.io.Serializable;
import motej.event.AccelerometerEvent;

/**
 *
 * @author salaboy
 */
public class WiiMoteEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private AccelerometerEvent event;
    private boolean processed;
    private String type;
    private int x;
    private int y;
    private int z;

    public WiiMoteEvent(AccelerometerEvent event) {
        this.event = event;
        this.processed = false;
        this.x = event.getX();
        this.y = event.getY();
        this.z = event.getZ();
    }

    public WiiMoteEvent(AccelerometerEvent event, String type) {
        this(event);
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    

    public AccelerometerEvent getEvent() {
        return event;
    }

    public void setEvent(AccelerometerEvent event) {
        this.event = event;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String toString(){
        return "[Event: "+" X = "+this.x+" Y = "+this.y+" Z = "+this.z+"]";
    }

}
