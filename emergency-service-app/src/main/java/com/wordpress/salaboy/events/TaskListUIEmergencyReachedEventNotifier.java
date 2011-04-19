/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.services.GridEmergencyService;
import com.wordpress.salaboy.ui.EmergencyFrame;
import com.wordpress.salaboy.events.MapEventsNotifier.EventType;
import com.wordpress.salaboy.ui.UserTaskListUI;

/**
 *
 * @author salaboy
 */
public class TaskListUIEmergencyReachedEventNotifier implements WorldEventNotifier{

    
    @Override
    public void notify(NotifierEvent event) {
        
        Long ambulanceId = ((EmergencyReachedNotifierEvent)event).getAmbulanceId();
        final EmergencyFrame emergencyFrameById = UserTaskListUI.getInstance().getCurrentEmergenciesPanel()
                                                          .getEmergencyFrameById(ambulanceId);
        emergencyFrameById.getPnlMedicalEvaluation().setEnabled(true); 
        emergencyFrameById.getMainTabPanel().setSelectedComponent(emergencyFrameById.getPnlMedicalEvaluation());       
        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_SELECTED, new TaskListUIHospitalSelectedEventNotifier());
    }

}
