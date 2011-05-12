/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.PatientAtHospitalEvent;
import com.wordpress.salaboy.model.events.PatientPickUpEvent;

/**
 *
 * @author salaboy
 */
public interface DefaultHeartAttackProcedure extends ProcedureService{
    public void patientAtHospitalNotification(PatientAtHospitalEvent event);
    public void patientPickUpNotification(PatientPickUpEvent event);
}
