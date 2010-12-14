/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.ui.CurrentEmergenciesPanel;
import javax.swing.JComponent;
import org.plugtree.training.model.Ambulance;

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
    public void notify(Object event) {
        Ambulance ambulance = CityEntitiesUtils.getAmbulanceById((Long)event);
        int newX = Math.round(ambulance.getPolygon().getX() / 16);
        int newY = Math.round(ambulance.getPolygon().getY() / 16); 
        String text = CityEntitiesUtils.translatePosition( newX, newY );
        if (!lastPositionText.equals(text) && !text.equals("N/A")){
            ((CurrentEmergenciesPanel)frame).getEmergencyFrameById(ambulance.getId()).getTxtPosition().insert(text+"\n", 0);
            lastPositionText = text;
        }
        
    }

}
