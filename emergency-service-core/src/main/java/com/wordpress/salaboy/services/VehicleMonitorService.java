/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.EmergencyVehicleEvent;

/**
 *
 * @author esteban
 */
public interface VehicleMonitorService {

    void newVehicleDispatched(final String emergencyId, final String vehicleId);

    void processEvent(EmergencyVehicleEvent event);

    void vehicleRemoved();
    
}
