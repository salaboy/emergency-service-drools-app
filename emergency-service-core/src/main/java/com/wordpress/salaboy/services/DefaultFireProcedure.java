/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.EmergencyEvent;
import com.wordpress.salaboy.model.events.FireExtinctedEvent;
import com.wordpress.salaboy.model.events.FireTruckOutOfWaterEvent;
import com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent;

/**
 *
 * @author esteban
 */
public interface DefaultFireProcedure extends ProcedureService{
    public void vehicleReachesEmergencyNotification(VehicleHitsEmergencyEvent event);
    public void fireTruckOutOfWaterNotification(FireTruckOutOfWaterEvent event);
    public void fireExtinctedNotification(FireExtinctedEvent event);
}
