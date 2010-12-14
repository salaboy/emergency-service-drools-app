/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.ui.EmergencyFrame;
import com.wordpress.salaboy.ui.UserTaskListUI;
import javax.swing.DefaultListModel;

/**
 *
 * @author salaboy
 */
public class TaskListUIMonitorAlertReceivedEventNotifier implements WorldEventNotifier{

    @Override
    public void notify(Object event) {
        String message = (String)event;
        //@TODO: I need the ambulanceId here too
        //Long ambulanceId = (Long)event;
        EmergencyFrame emergencyFrame = UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(0L);
        emergencyFrame.getAlerts().add(0,message);
        
        DefaultListModel model = new DefaultListModel();
        for (String alert : emergencyFrame.getAlerts()) {
            model.addElement(alert);
        }
        emergencyFrame.getLstAlerts().setModel(model);
        emergencyFrame.refresh();
    }

}
