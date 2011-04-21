/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.events;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.services.GridEmergencyService;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableAmbulance;
import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.PositionUpdatedNotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;
import com.wordpress.salaboy.model.Ambulance;

/**
 *
 * @author salaboy
 */
public class MapAmbulancePositionUpdatedEventNotifier implements WorldEventNotifier{

    
    @Override
    public void notify(NotifierEvent event) {
        
        Ambulance ambulance = CityEntitiesUtils.getAmbulanceById(((PositionUpdatedNotifierEvent)event).getAmbulanceId());
        GraphicableAmbulance graphAmbulance = null; //UserTaskListUI.getInstance().getGame().getGraphicableAmbulanceById(((PositionUpdatedNotifierEvent)event).getAmbulanceId());
        float newX = Math.round(graphAmbulance.getPolygon().getX() / 16);
        float newY = Math.round(graphAmbulance.getPolygon().getY() / 16);
        
        if(newX != ambulance.getPositionX() || newY != ambulance.getPositionY()){
            ambulance.setPositionX(newX);
            ambulance.setPositionY(newY);
            GridEmergencyService.getInstance().updateAmbualancePosition(ambulance);
            
        }
    }

}
