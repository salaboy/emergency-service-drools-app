/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.events;

import java.io.Serializable;

/**
 * Represents an event related to a Vehicle in an Emergency
 * @author esteban
 */
public interface EmergencyVehicleEvent extends EmergencyEvent, Serializable {
    public String getVehicleId();
}
