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
import org.plugtree.training.model.Hospital;

/**
 *
 * @author salaboy
 */
public class TaskListUIHospitalSelectedEventNotifier implements WorldEventNotifier {

    @Override
    public void notify(Object event) {
        // This is the hospital Selected ID
        Long hospitalId = (Long)event;
        //@TODO: I should also have the ambulance ID.. to be able to select the correct frame
        
        EmergencyFrame emergencyFrame = UserTaskListUI.getInstance().getCurrentEmergenciesPanel().getEmergencyFrameById(0L);
        emergencyFrame.getLblDirection().setForeground(Color.green);
        
        
        Hospital hospital = CityEntitiesUtils.getHospitalById(hospitalId);
        
        int x = (int)hospital.getPositionX();
        int y = (int)hospital.getPositionY();
        
        String text = "Hospital at "+x+" - "+y;
        
        emergencyFrame.getLblDirection().setText(text);
        emergencyFrame.setEmergencyMonitorPanel(new EmergencyMonitorPanel(emergencyFrame));
        
        System.out.println("Hospital Selected = "+hospital.toString());
        UserTaskListUI.getInstance().getGame().addHospital(GraphicableFactory.newHighlightedHospital(CityEntitiesUtils.getHospitalById(hospitalId)));
        
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_BEAT_RECEIVED, new TaskListUIPatientVitalSignReceivedEventNotifier());
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.NO_VITAL_SIGNS, new TaskListUIMonitorAlertReceivedEventNotifier());
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HEART_ATTACK, new TaskListUIMonitorAlertReceivedEventNotifier());
        EmergencyService.getInstance().getMapEventsNotifier().addWorldEventNotifier(EventType.HOSPITAL_REACHED, new TaskListUIHospitalReachedEventNotifier());
        
        
    }

}
