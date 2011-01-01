/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.ui.UserTaskListUI;

/**
 *
 * @author salaboy
 */
public class TaskListUIPatientVitalSignReceivedEventNotifier implements WorldEventNotifier{

    @Override
    public void notify(NotifierEvent event) {
        PulseEvent evt = ((PatientVitalSignNotifierEvent)event).getHeartBeatEvent();
        Long ambulanceId = ((PatientVitalSignNotifierEvent)event).getAmbulanceId();
        UserTaskListUI.getInstance().getCurrentEmergenciesPanel().onHeartBeatReceived(ambulanceId, evt.getValue());
    }

}
