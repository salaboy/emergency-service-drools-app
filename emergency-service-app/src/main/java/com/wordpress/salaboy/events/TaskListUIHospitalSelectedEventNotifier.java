/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.EmergencyService;
import com.wordpress.salaboy.graphicable.GraphicableFactory;
import com.wordpress.salaboy.ui.EmergencyFrame;
import com.wordpress.salaboy.ui.EmergencyMonitorPanel;
import com.wordpress.salaboy.ui.MapEventsNotifier.EventType;
import com.wordpress.salaboy.ui.UserTaskListUI;
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
        
        
        EmergencyFrame emergencyFrame = UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(ambulanceId);
        emergencyFrame.getLblDirection().setForeground(Color.green);
        
        
        Hospital hospital = CityEntitiesUtils.getHospitalById(hospitalId);
        
        int x = (int)hospital.getPositionX();
        int y = (int)hospital.getPositionY();
        
        String text = "Hospital at "+x+" - "+y;
        
        emergencyFrame.getLblDirection().setText(text);
        System.out.println("\t\tTaskListUIHospitalSelectedEventNotifier ("+this+") adding a new monitor panel");
        emergencyFrame.setEmergencyMonitorPanel(new EmergencyMonitorPanel(emergencyFrame));
        
        System.out.println("Hospital Selected = "+hospital.toString());
        UserTaskListUI.getInstance().getGame().addHospital(GraphicableFactory.newHighlightedHospital(CityEntitiesUtils.getHospitalById(hospitalId)));
        
        TaskListUIMonitorAlertReceivedEventNotifier taskListUIMonitorAlertReceivedEventNotifier = new TaskListUIMonitorAlertReceivedEventNotifier();
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_BEAT_RECEIVED, new TaskListUIPatientVitalSignReceivedEventNotifier());
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.NO_VITAL_SIGNS, taskListUIMonitorAlertReceivedEventNotifier);
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_ATTACK, taskListUIMonitorAlertReceivedEventNotifier );
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_REACHED, new TaskListUIHospitalReachedEventNotifier());
        
        
    }

}
