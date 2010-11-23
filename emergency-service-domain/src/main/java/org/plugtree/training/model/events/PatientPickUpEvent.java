/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.plugtree.training.model.events;

import java.io.Serializable;
import java.util.Date;
import org.plugtree.training.model.Ambulance;

/**
 *
 * @author salaboy
 */
public class PatientPickUpEvent implements Serializable {
    private transient Ambulance ambulance;
    private Date time;
    

    public PatientPickUpEvent() {
    }

    public PatientPickUpEvent(Ambulance ambulance, Date time) {
        this.ambulance = ambulance;
        this.time = time;
    }

    public Ambulance getAmbulance() {
        return ambulance;
    }

    public void setAmbulance(Ambulance ambulance) {
        this.ambulance = ambulance;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    
    
    

}
