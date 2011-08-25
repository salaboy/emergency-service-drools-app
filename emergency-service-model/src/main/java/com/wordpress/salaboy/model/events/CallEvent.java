/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.events;

import java.io.Serializable;

/**
 * Represents an event related to a Call
 * @author esteban
 */
public interface CallEvent extends Serializable {
    public String getCallId();
}
