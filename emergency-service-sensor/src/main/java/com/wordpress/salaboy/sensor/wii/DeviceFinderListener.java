/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor.wii;

import java.util.EventListener;

/**
 *
 * @author salaboy
 */
public interface DeviceFinderListener extends EventListener{
    public void deviceFound(Device device);
}
