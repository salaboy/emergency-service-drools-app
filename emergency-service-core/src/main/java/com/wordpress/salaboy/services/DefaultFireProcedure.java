/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.*;

/**
 *
 * @author esteban
 */
public interface DefaultFireProcedure extends ProcedureService{
    public void vehicleReachesEmergencyNotification(VehicleHitsEmergencyEvent event);
    public void fireTruckOutOfWaterNotification(FireTruckOutOfWaterEvent event);
    public void fireExtinctedNotification(FireExtinctedEvent event);
    public void vehicleHitsFireDepartmentEventNotification(VehicleHitsFireDepartmentEvent vehicleHitsFireDepartmentEvent);
}
