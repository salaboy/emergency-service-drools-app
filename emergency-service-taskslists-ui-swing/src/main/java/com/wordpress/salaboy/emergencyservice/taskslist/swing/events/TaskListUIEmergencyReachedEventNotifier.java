/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.taskslist.swing.events;

import com.wordpress.salaboy.services.old.GridEmergencyService;
import com.wordpress.salaboy.emergencyservice.extrapanels.EmergencyFrame;
import com.wordpress.salaboy.events.MapEventsNotifier.EventType;
import com.wordpress.salaboy.emergencyservice.main.UserTaskListUI;
import com.wordpress.salaboy.events.EmergencyReachedNotifierEvent;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;

/**
 *
 * @author salaboy
 */
public class TaskListUIEmergencyReachedEventNotifier implements WorldEventNotifier{

    
    @Override
    public void notify(NotifierEvent event) {
        
        Long ambulanceId = ((EmergencyReachedNotifierEvent)event).getAmbulanceId();
//        final EmergencyFrame emergencyFrameById = UserTaskListUI.getInstance().getCurrentEmergenciesPanel()
//                                                          .getEmergencyFrameById(ambulanceId);
//        emergencyFrameById.getPnlMedicalEvaluation().setEnabled(true); 
//        emergencyFrameById.getMainTabPanel().setSelectedComponent(emergencyFrameById.getPnlMedicalEvaluation());       
//        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_SELECTED, new TaskListUIHospitalSelectedEventNotifier());
    }

}
