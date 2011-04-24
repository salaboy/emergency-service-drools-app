/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.events.keyboard.KeyboardPulseEventGenerator;
import java.util.Map.Entry;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;

/**
 *
 * @author esteban
 */
public class WorldMouseListener implements MouseListener {

    private final WorldUI worldUI;

    WorldMouseListener(WorldUI cityMapUI) {
        this.worldUI = cityMapUI;
    }

    public void mouseWheelMoved(int i) {
    }

    public void mouseClicked(int button, int x, int y, int count) {
        if (Input.MOUSE_LEFT_BUTTON == button){
            for (Entry<Long, GraphicableEmergency> entry : this.worldUI.getEmergencies().entrySet()) {
                if (entry.getValue().getPolygon().contains(x,y)){
                    this.worldUI.emergencyClicked(entry.getKey());
                    //assumes that a single click only collides with only one emergency
                    return;
                }
            }
        }
    }

    public void mousePressed(int i, int i1, int i2) {
    }

    public void mouseReleased(int i, int i1, int i2) {
    }

    public void mouseMoved(int i, int i1, int i2, int i3) {
    }

    public void mouseDragged(int i, int i1, int i2, int i3) {
    }

    public void setInput(Input input) {
    }

    public boolean isAcceptingInput() {
        return true;
    }

    public void inputEnded() {
    }

    public void inputStarted() {
    }
}
