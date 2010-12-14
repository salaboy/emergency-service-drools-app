/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.EmergencyService;
import com.wordpress.salaboy.ui.MapEventsNotifier.EventType;
import com.wordpress.salaboy.ui.UserTaskListUI;

/**
 *
 * @author salaboy
 */
public class TaskListUIEmergencyReachedEventNotifier implements WorldEventNotifier{

    
    @Override
    public void notify(Object event) {
        
        Long id = (Long)event;
        UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(id).getPnlMedicalEvaluation().setEnabled(true); 
        UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(id).getMainTabPanel().setSelectedComponent(UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(id).getPnlMedicalEvaluation());       
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_SELECTED, new TaskListUIHospitalSelectedEventNotifier());
    }

}
