/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

/**
 *
 * @author salaboy
 */
public interface NotifierEvent {
    public String getEventType();
    public Long getEventId();
}
