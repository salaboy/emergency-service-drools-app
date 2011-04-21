/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;
import com.wordpress.salaboy.model.Ambulance;

/**
 *
 * @author salaboy
 */
public class GraphicableAmbulance implements Graphicable{
    private Ambulance ambulance;
    private Animation animation;
    private Polygon   polygon;
    
    public GraphicableAmbulance(Ambulance ambulance) {
        this.ambulance = ambulance;
    }

    public Ambulance getAmbulance() {
        return ambulance;
    }

    public void setAmbulance(Ambulance ambulance) {
        this.ambulance = ambulance;
    }

    @Override
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public Polygon getPolygon() {
        return this.polygon;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
    
    
    
        
    
}
