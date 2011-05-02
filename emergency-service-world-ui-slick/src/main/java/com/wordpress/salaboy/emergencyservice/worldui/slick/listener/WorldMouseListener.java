/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick.listener;

import com.wordpress.salaboy.emergencyservice.worldui.slick.WorldUI;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;

/**
 *
 * @author esteban
 */
public class WorldMouseListener implements MouseListener {

    private final WorldUI worldUI;

    public WorldMouseListener(WorldUI cityMapUI) {
        this.worldUI = cityMapUI;
    }

    public void mouseWheelMoved(int i) {
    }

    public void mouseClicked(int button, int x, int y, int count) {
        this.worldUI.getCurrentRenderer().onClick(button, x, y, count);
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
