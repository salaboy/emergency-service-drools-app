/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.taskslist.swing.events;

import com.wordpress.salaboy.emergencyservice.extrapanels.EmergencyFrame;
import com.wordpress.salaboy.emergencyservice.main.UserTaskListUI;
import com.wordpress.salaboy.events.MonitorAlertReceivedNotifierEvent;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;
import javax.swing.DefaultListModel;

/**
 *
 * @author salaboy
 */
public class TaskListUIMonitorAlertReceivedEventNotifier implements WorldEventNotifier{

    @Override
    public void notify(NotifierEvent event) {
        String message = ((MonitorAlertReceivedNotifierEvent)event).getMessage();
        Long emergencyId = ((MonitorAlertReceivedNotifierEvent)event).getEmergencyId();
        //@TODO: I need the ambulanceId here too
        //Long ambulanceId = (Long)event;
        EmergencyFrame emergencyFrame = null;//UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(emergencyId);
        emergencyFrame.getAlerts().add(0,message);
        
        DefaultListModel model = new DefaultListModel();
        for (String alert : emergencyFrame.getAlerts()) {
            model.addElement(alert);
        }
        emergencyFrame.getLstAlerts().setModel(model);
        emergencyFrame.refresh();
    }

}
