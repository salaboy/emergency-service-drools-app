/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.model.Emergency;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public void renderPolygon(GameContainer gc, Graphics g) {
        for (GraphicableEmergency renderEmergency : ui.getEmergencies().values()) {
            g.draw(renderEmergency.getPolygon());
        }
    }

    @Override
    public void renderAnimation(GameContainer gc, Graphics g) {
        for (GraphicableEmergency renderEmergency : ui.getEmergencies().values()) {
            g.drawAnimation(renderEmergency.getAnimation(), renderEmergency.getPolygon().getX(), renderEmergency.getPolygon().getY());
        }
    }

    @Override
    public void onKeyPressed(int code, char key) {
        try {
            if (Input.KEY_SPACE == code) {
                this.ui.addRandomGenericEmergency();
            } else if (Input.KEY_ENTER == code) {
                this.ui.addRandomEmergency(Emergency.EmergencyType.FIRE, 10);
            } else if (Input.KEY_V == code) {
                this.ui.addXYEmergency(2,1);
                this.ui.addXYEmergency(2,2);
            }else if (Input.KEY_ESCAPE == code) {
                this.ui.goToGlobalMap();
            }
        } catch (IOException ex) {
            Logger.getLogger(GlobalEmergenciesRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onKeyReleased(int code, char key) {
    }

    @Override
    public void onClick(int button, int x, int y, int count) {
        if (Input.MOUSE_LEFT_BUTTON == button) {
            for (Entry<String, GraphicableEmergency> entry : ui.getEmergencies().entrySet()) {
                if (entry.getValue().getPolygon().contains(x, y)) {
                    this.ui.emergencyClicked(entry.getKey());
                    //assumes that a single click only collides with only one emergency
                    return;
                }
            }
        }
    }

    @Override
    public void update(GameContainer gc, int delta) {
    }

    @Override
    public void renderHighlightsAnimation(GameContainer gc, Graphics g) {
        // DO NOTHING HERE
    }
}
