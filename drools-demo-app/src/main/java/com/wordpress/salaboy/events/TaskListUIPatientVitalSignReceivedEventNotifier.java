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
    public void notify(NotifierEvent event) {
        WiiMoteEvent evt = ((PatientVitalSignNotifierEvent)event).getHeartBeatEvent();
        Long ambulanceId = ((PatientVitalSignNotifierEvent)event).getAmbulanceId();
        EmergencyMonitorPanel emergencyMonitorPanel = UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(ambulanceId).getEmergencyMonitorPanel();
         if (emergencyMonitorPanel != null){
            emergencyMonitorPanel.updateMonitorGraph(evt.getY());
        }
    }

}
