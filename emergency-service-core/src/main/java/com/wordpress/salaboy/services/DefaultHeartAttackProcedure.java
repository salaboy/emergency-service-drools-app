/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.VehicleHitsHospitalEvent;
import com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent;

/**
 *
 * @author salaboy
 */
public interface DefaultHeartAttackProcedure extends ProcedureService{
    public void patientAtHospitalNotification(VehicleHitsHospitalEvent event);
    public void patientPickUpNotification(VehicleHitsEmergencyEvent event);
}
