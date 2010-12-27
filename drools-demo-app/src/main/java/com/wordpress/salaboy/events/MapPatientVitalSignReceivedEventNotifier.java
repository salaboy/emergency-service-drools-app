/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.EmergencyService;

/**
 *
 * @author salaboy
 */
public class MapPatientVitalSignReceivedEventNotifier implements WorldEventNotifier {

    @Override
    public void notify(NotifierEvent event) {
        
        PulseEvent evt = ((PatientVitalSignNotifierEvent)event).getHeartBeatEvent();
        Long ambulanceId  = ((PatientVitalSignNotifierEvent)event).getAmbulanceId();
        
        EmergencyService.getInstance().heartBeatReceivedFromAmbulance(ambulanceId, evt);
        
    }

}
