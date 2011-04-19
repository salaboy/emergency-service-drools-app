/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.services.GridEmergencyService;
import com.wordpress.salaboy.graphicable.GraphicableAmbulance;
import com.wordpress.salaboy.ui.UserTaskListUI;
import com.wordpress.salaboy.model.Ambulance;

/**
 *
 * @author salaboy
 */
public class MapAmbulancePositionUpdatedEventNotifier implements WorldEventNotifier{

    
    @Override
    public void notify(NotifierEvent event) {
        
        Ambulance ambulance = CityEntitiesUtils.getAmbulanceById(((PositionUpdatedNotifierEvent)event).getAmbulanceId());
        GraphicableAmbulance graphAmbulance = UserTaskListUI.getInstance().getGame().getGraphicableAmbulanceById(((PositionUpdatedNotifierEvent)event).getAmbulanceId());
        float newX = Math.round(graphAmbulance.getPolygon().getX() / 16);
        float newY = Math.round(graphAmbulance.getPolygon().getY() / 16);
        
        if(newX != ambulance.getPositionX() || newY != ambulance.getPositionY()){
            ambulance.setPositionX(newX);
            ambulance.setPositionY(newY);
            GridEmergencyService.getInstance().updateAmbualancePosition(ambulance);
            
        }
    }

}
