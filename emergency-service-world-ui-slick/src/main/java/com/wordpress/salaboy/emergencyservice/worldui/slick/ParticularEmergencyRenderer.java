/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 *
 * @author esteban
 */
public class ParticularEmergencyRenderer implements EmergencyRenderer {
    
    private final GraphicableEmergency emergency;

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
    }

    public void renderAnimation(WorldUI ui, GameContainer gc, Graphics g) {
        g.drawAnimation(emergency.getAnimation(), emergency.getPolygon().getX(), emergency.getPolygon().getY());
    }
}
