/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.services.GridEmergencyService;

/**
 *
 * @author salaboy
 */
public class MapPatientVitalSignReceivedEventNotifier implements WorldEventNotifier {

    @Override
    public void notify(NotifierEvent event) {
        
        PulseEvent evt = ((PatientVitalSignNotifierEvent)event).getHeartBeatEvent();
        Long ambulanceId  = ((PatientVitalSignNotifierEvent)event).getAmbulanceId();
        
        GridEmergencyService.getInstance().heartBeatReceivedFromAmbulance(ambulanceId, evt);
        
    }

}
