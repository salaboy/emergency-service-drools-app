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
public class TaskListUIHospitalReachedEventNotifier implements WorldEventNotifier{

    

    public TaskListUIHospitalReachedEventNotifier() {
    
    }
    
    
    @Override
    public void notify(NotifierEvent event) {
        UserTaskListUI.getInstance().getMainJTabbedPane().setSelectedIndex(4);
        UserTaskListUI.getInstance().refreshPatientsTable();
    }

}
