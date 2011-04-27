/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.taskslist.swing.events;

import com.wordpress.salaboy.emergencyservice.main.UserTaskListUI;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.PatientVitalSignNotifierEvent;
import com.wordpress.salaboy.events.PulseEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;

/**
 *
 * @author salaboy
 */
public class TaskListUIPatientVitalSignReceivedEventNotifier implements WorldEventNotifier{

    @Override
    public void notify(NotifierEvent event) {
        PulseEvent evt = ((PatientVitalSignNotifierEvent)event).getHeartBeatEvent();
        Long ambulanceId = ((PatientVitalSignNotifierEvent)event).getAmbulanceId();
    //    UserTaskListUI.getInstance().getCurrentEmergenciesPanel().onHeartBeatReceived(ambulanceId, evt.getValue());
    }

}
