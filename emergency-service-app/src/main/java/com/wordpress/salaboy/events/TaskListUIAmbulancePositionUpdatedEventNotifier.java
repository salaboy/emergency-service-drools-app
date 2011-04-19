/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.PositionUpdatedNotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;
import com.wordpress.salaboy.events.WorldEventNotifier;
import com.wordpress.salaboy.graphicable.GraphicableAmbulance;
import com.wordpress.salaboy.ui.CurrentEmergenciesPanel;
import com.wordpress.salaboy.ui.UserTaskListUI;
import javax.swing.JComponent;

/**
 *
 * @author salaboy
 */
public class TaskListUIAmbulancePositionUpdatedEventNotifier implements WorldEventNotifier {

    
    private JComponent frame;
    private String lastPositionText = "";
    public TaskListUIAmbulancePositionUpdatedEventNotifier(JComponent frame) {
        this.frame = frame;
    }
    
    @Override
    public void notify(NotifierEvent event) {
        Long ambulanceId = ((PositionUpdatedNotifierEvent)event).getAmbulanceId();
        GraphicableAmbulance ambulance = UserTaskListUI.getInstance().getGame().getGraphicableAmbulanceById(ambulanceId);
        int newX = Math.round(ambulance.getPolygon().getX() / 16);
        int newY = Math.round(ambulance.getPolygon().getY() / 16); 
        String text = CityEntitiesUtils.translatePosition( newX, newY );
        if (!lastPositionText.equals(text) && !text.equals("N/A")){
            ((CurrentEmergenciesPanel)frame).getEmergencyFrameById(ambulance.getAmbulance().getId()).getTxtPosition().insert(text+"\n", 0);
            lastPositionText = text;
        }
        
    }

}
