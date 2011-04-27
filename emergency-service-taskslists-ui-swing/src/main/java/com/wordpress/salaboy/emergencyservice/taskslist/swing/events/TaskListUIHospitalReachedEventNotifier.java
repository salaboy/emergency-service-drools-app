/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.taskslist.swing.events;

import com.wordpress.salaboy.emergencyservice.main.UserTaskListUI;
import com.wordpress.salaboy.events.HospitalReachedNotifierEvent;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;


/**
 *
 * @author salaboy
 */
public class TaskListUIHospitalReachedEventNotifier implements WorldEventNotifier{

    

    public TaskListUIHospitalReachedEventNotifier() {
    
    }
    
    
    @Override
    public void notify(NotifierEvent event) {
//        UserTaskListUI.getInstance().getMainJTabbedPane().setSelectedIndex(4);
//        UserTaskListUI.getInstance().refreshPatientsTable();
//        UserTaskListUI.getInstance().getCurrentEmergenciesPanel().onHospitalReached(((HospitalReachedNotifierEvent)event).getAmbulanceId());
    }

}
