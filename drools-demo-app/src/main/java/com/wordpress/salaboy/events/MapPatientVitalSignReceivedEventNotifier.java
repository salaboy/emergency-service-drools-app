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
    public void notify(Object event) {
        WiiMoteEvent evt = (WiiMoteEvent)event;
        EmergencyService.getInstance().heartBeatReceivedFromAmbulance(
                                                //evt.getAmbulanceId(), 
                                                evt);
        
    }

}
