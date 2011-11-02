/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 *
 * @author esteban
 */
public interface EmergencyRenderer {

    void renderPolygon(GameContainer gc, Graphics g);
    void renderAnimation(GameContainer gc, Graphics g);
    void renderHighlightsAnimation(GameContainer gc, Graphics g);
    void update(GameContainer gc, int delta);
    void onKeyPressed(int code, char key);
    void onKeyReleased(int code, char key);
    void onClick(int button, int x, int y, int count);
    
}
