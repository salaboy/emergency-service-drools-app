/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.EmergencyService;
import java.util.Date;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.events.PatientPickUpEvent;

/**
 *
 * @author salaboy
 */
public class MapEmergencyReachedEventNotifier implements WorldEventNotifier{
    
    private Emergency emergency;

    public MapEmergencyReachedEventNotifier(Emergency emergency) {
        this.emergency = emergency;
    }
    
    
    @Override
    public void notify(NotifierEvent event) {
        EmergencyService.getInstance().sendPatientPickUpEvent(new PatientPickUpEvent(new Date()), this.emergency.getCall().getProcessId());
    }

}
