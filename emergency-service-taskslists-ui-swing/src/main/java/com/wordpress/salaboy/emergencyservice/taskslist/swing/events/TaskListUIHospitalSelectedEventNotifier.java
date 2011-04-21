/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.taskslist.swing.events;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.emergencyservice.taskslist.swing.EmergencyFrame;
import com.wordpress.salaboy.emergencyservice.taskslist.swing.EmergencyMonitorPanel;
import com.wordpress.salaboy.events.HospitalSelectedNotifierEvent;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;
import java.awt.Color;
import com.wordpress.salaboy.model.Hospital;

/**
 *
 * @author salaboy
 */
public class TaskListUIHospitalSelectedEventNotifier implements WorldEventNotifier {

    @Override
    public void notify(NotifierEvent event) {
        // This is the hospital Selected ID
        Long hospitalId = ((HospitalSelectedNotifierEvent)event).getHospitalId();
        Long ambulanceId = ((HospitalSelectedNotifierEvent)event).getAmbulanceId();
        
        
        EmergencyFrame emergencyFrame = null;//UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(ambulanceId);
        emergencyFrame.getLblDirection().setForeground(Color.green);
        
        
        Hospital hospital = CityEntitiesUtils.getHospitalById(hospitalId);
        
        int x = (int)hospital.getPositionX();
        int y = (int)hospital.getPositionY();
        
        String text = "Hospital at "+x+" - "+y;
        
        emergencyFrame.getLblDirection().setText(text);
        System.out.println("\t\tTaskListUIHospitalSelectedEventNotifier ("+this+") adding a new monitor panel");
        emergencyFrame.setEmergencyMonitorPanel(new EmergencyMonitorPanel(emergencyFrame));
        
        System.out.println("Hospital Selected = "+hospital.toString());
       // UserTaskListUI.getInstance().getGame().addHospital(GraphicableFactory.newHighlightedHospital(CityEntitiesUtils.getHospitalById(hospitalId)));
        
//        TaskListUIMonitorAlertReceivedEventNotifier taskListUIMonitorAlertReceivedEventNotifier = new TaskListUIMonitorAlertReceivedEventNotifier();
//        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_BEAT_RECEIVED, new TaskListUIPatientVitalSignReceivedEventNotifier());
//        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.NO_VITAL_SIGNS, taskListUIMonitorAlertReceivedEventNotifier);
//        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_ATTACK, taskListUIMonitorAlertReceivedEventNotifier );
//        GridEmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_REACHED, new TaskListUIHospitalReachedEventNotifier());
        
        
    }

}
