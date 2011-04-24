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
public class GlobalEmergenciesRenderer implements EmergencyRenderer {
    
    /**
     * Renders all the emergencies
     * @param ui 
     */
    @Override
    public void renderPolygon(WorldUI ui, GameContainer gc, Graphics g){
        for (GraphicableEmergency renderEmergency : ui.getEmergencies().values()) {
            g.draw(renderEmergency.getPolygon());
        }
    }

    public void renderAnimation(WorldUI ui, GameContainer gc, Graphics g) {
        for (GraphicableEmergency renderEmergency : ui.getEmergencies().values()) {
            g.drawAnimation(renderEmergency.getAnimation(), renderEmergency.getPolygon().getX(), renderEmergency.getPolygon().getY());
        }
    }

}
