/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;


import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.EmergencyService;
import com.wordpress.salaboy.ui.AmbulanceMonitorService;
import com.wordpress.salaboy.ui.Block;
import com.wordpress.salaboy.ui.BlockMap;
import com.wordpress.salaboy.ui.MapEventsNotifier.EventType;
import com.wordpress.salaboy.ui.UserTaskListUI;
import org.plugtree.training.model.Hospital;

/**
 *
 * @author salaboy
 */
public class MapHospitalSelectedEventNotifier implements WorldEventNotifier{

    

    public MapHospitalSelectedEventNotifier() {
       
    }
    
    
    @Override
    public void notify(Object event) {
        //@TODO: i need the ambulance ID here too
        //@TODO: i need the emergency ID too
        Hospital selectedHospital = null;
        Long hospitalId = (Long)event;
        
        int hospitasquare[] = {1, 1, 15, 1, 15, 15, 1, 15};
        
        for (Hospital hospitalnow : CityEntitiesUtils.hospitals.values()) {
            if (hospitalnow.getId() == hospitalId) {
                selectedHospital = hospitalnow;
            }
        }
        
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_REACHED, new MapHospitalReachedEventNotifier(hospitalId));

        BlockMap.hospitals.add(new Block(Math.round(selectedHospital.getPositionX()) * 16, Math.round(selectedHospital.getPositionY()) * 16, hospitasquare, "hospital"));

        
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_BEAT_RECEIVED, new MapPatientVitalSignReceivedEventNotifier());
        
        
        //START the monitor service with the Ambulance ID
        AmbulanceMonitorService.getInstance().start();
        
        UserTaskListUI.getInstance().getGame().removeEmergency(0L);
    }

}
