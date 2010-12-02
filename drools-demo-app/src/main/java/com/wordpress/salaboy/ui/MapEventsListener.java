/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

/**
 *
 * @author esteban
 */
public interface MapEventsListener {
    void hospitalReached(Block hospital);
    void emergencyReached(Block emergency);
    void positionReceived(Block corner);
    void hospitalSelected(Long id);
    void heartBeatReceived(double value);
}
