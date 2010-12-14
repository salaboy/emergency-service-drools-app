/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.EmergencyService;
import org.plugtree.training.model.Ambulance;

/**
 *
 * @author salaboy
 */
public class MapAmbulancePositionUpdatedEventNotifier implements WorldEventNotifier{

    

    public MapAmbulancePositionUpdatedEventNotifier() {
        
    }
    
    
    
    
    @Override
    public void notify(Object event) {
        Ambulance ambulance = CityEntitiesUtils.getAmbulanceById((Long)event);
        float newX = Math.round(ambulance.getPolygon().getX() / 16);
        float newY = Math.round(ambulance.getPolygon().getY() / 16);
        
        if(newX != ambulance.getPositionX() || newY != ambulance.getPositionY()){
            ambulance.setPositionX(newX);
            ambulance.setPositionY(newY);
            EmergencyService.getInstance().updateAmbualancePosition(ambulance);
            
        }
    }

}
