/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.EmergencyService;
import com.wordpress.salaboy.ui.EmergencyFrame;
import com.wordpress.salaboy.ui.MapEventsNotifier.EventType;
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
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_SELECTED, new TaskListUIHospitalSelectedEventNotifier());
    }

}
