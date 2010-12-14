/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.ui.EmergencyMonitorPanel;
import com.wordpress.salaboy.ui.UserTaskListUI;

/**
 *
 * @author salaboy
 */
public class TaskListUIPatientVitalSignReceivedEventNotifier implements WorldEventNotifier{

    @Override
    public void notify(Object event) {
        WiiMoteEvent evt = (WiiMoteEvent)event;
        //THe event should contain the ambulance ID
        EmergencyMonitorPanel emergencyMonitorPanel = UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById((Long)0L).getEmergencyMonitorPanel();
         if (emergencyMonitorPanel != null){
            emergencyMonitorPanel.updateMonitorGraph(evt.getY());
        }
    }

}
