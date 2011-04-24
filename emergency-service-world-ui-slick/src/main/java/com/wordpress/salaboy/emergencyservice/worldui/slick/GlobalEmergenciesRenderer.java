/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import java.util.Map.Entry;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 *
 * @author esteban
 */
public class GlobalEmergenciesRenderer implements EmergencyRenderer {
    private final WorldUI ui;

    public GlobalEmergenciesRenderer(WorldUI ui) {
        this.ui = ui;
    }
    
    /**
     * Renders all the emergencies
     * @param ui 
     */
    @Override
    public void renderPolygon(GameContainer gc, Graphics g){
        for (GraphicableEmergency renderEmergency : ui.getEmergencies().values()) {
            g.draw(renderEmergency.getPolygon());
        }
    }

    public void renderAnimation(GameContainer gc, Graphics g) {
        for (GraphicableEmergency renderEmergency : ui.getEmergencies().values()) {
            g.drawAnimation(renderEmergency.getAnimation(), renderEmergency.getPolygon().getX(), renderEmergency.getPolygon().getY());
        }
    }

    public void onKeyPressed(int code, char key) {
        if (Input.KEY_SPACE == code) {
            this.ui.addRandomEmergency();
        } else if (Input.KEY_ESCAPE == code) {
            this.ui.goToGlobalMap();
        }
    }

    public void onClick(int button, int x, int y, int count) {
        if (Input.MOUSE_LEFT_BUTTON == button){
            for (Entry<Long, GraphicableEmergency> entry : ui.getEmergencies().entrySet()) {
                if (entry.getValue().getPolygon().contains(x,y)){
                    this.ui.emergencyClicked(entry.getKey());
                    //assumes that a single click only collides with only one emergency
                    return;
                }
            }
        }
    }

}
