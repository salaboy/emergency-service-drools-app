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

    void renderPolygon(WorldUI ui, GameContainer gc, Graphics g);
    void renderAnimation(WorldUI ui, GameContainer gc, Graphics g);
    
}
