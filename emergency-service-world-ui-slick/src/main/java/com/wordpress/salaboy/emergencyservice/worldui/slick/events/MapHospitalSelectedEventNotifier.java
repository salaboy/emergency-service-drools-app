/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.events;


import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.services.GridEmergencyService;
import com.wordpress.salaboy.emergencyservice.worldui.slick.Block;
import com.wordpress.salaboy.emergencyservice.worldui.slick.BlockMap;
import com.wordpress.salaboy.events.MapEventsNotifier.EventType;
import com.wordpress.salaboy.events.HospitalSelectedNotifierEvent;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;
import com.wordpress.salaboy.model.Hospital;

/**
 *
 * @author salaboy
 */
public class MapHospitalSelectedEventNotifier implements WorldEventNotifier{

    

    public MapHospitalSelectedEventNotifier() {
       
    }
    
    
    @Override
    public void notify(NotifierEvent event) {
        
        Hospital selectedHospital = null;
        Long hospitalId = ((HospitalSelectedNotifierEvent)event).getHospitalId();
        Long ambulanceId = ((HospitalSelectedNotifierEvent)event).getAmbulanceId();
        Long emergencyId = ((HospitalSelectedNotifierEvent)event).getEmergencyId();
        
        int hospitasquare[] = {1, 1, 15, 1, 15, 15, 1, 15};
        
        for (Hospital hospitalnow : CityEntitiesUtils.hospitals.values()) {
            if (hospitalnow.getId() == hospitalId) {
                selectedHospital = hospitalnow;
            }
        }
        
        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_REACHED, new MapHospitalReachedEventNotifier(hospitalId));

        BlockMap.hospitals.add(new Block(Math.round(selectedHospital.getPositionX()) * 16, Math.round(selectedHospital.getPositionY()) * 16, hospitasquare, "hospital"));

        
        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_BEAT_RECEIVED, new MapPatientVitalSignReceivedEventNotifier());
        
        
        //@TODO: START the monitor service with the Ambulance ID
       // AmbulanceMonitorService.getInstance().start();
        
       // UserTaskListUI.getInstance().getGame().removeEmergency(emergencyId);
    }

}
