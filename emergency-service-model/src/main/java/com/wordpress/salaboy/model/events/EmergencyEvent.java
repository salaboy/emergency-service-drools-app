/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.events;

import java.io.Serializable;

/**
 * Represents an event related to a Call/ Emergency????
 * @author esteban
 */
public interface EmergencyEvent extends Serializable {
    public String getEmergencyId();
}
