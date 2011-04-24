/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableAmbulance;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableFactory;
import com.wordpress.salaboy.model.Ambulance;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 *
 * @author esteban
 */
public class ParticularEmergencyRenderer implements EmergencyRenderer {
    
    private final GraphicableEmergency emergency;
    private GraphicableAmbulance ambulance;

    public ParticularEmergencyRenderer(GraphicableEmergency emergency) {
        this.emergency = emergency;
    }
    
    /**
     * 
     * @param ui 
     */
    @Override
    public void renderPolygon(WorldUI ui, GameContainer gc, Graphics g){
        g.draw(emergency.getPolygon());
        if (ambulance != null){
            g.draw(ambulance.getPolygon());
        }
    }

    public void renderAnimation(WorldUI ui, GameContainer gc, Graphics g) {
        g.drawAnimation(emergency.getAnimation(), emergency.getPolygon().getX(), emergency.getPolygon().getY());
        if (ambulance != null){
            g.drawAnimation(ambulance.getAnimation(), ambulance.getPolygon().getX(), ambulance.getPolygon().getY());
        }
    }

    public void addVehicle(Ambulance vehicle) {
        ambulance = GraphicableFactory.newAmbulance(vehicle);
    }
}
