/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author salaboy
 */
public class GraphicableEmergency implements Graphicable{
    private Polygon polygon;
    private Animation animation;
    
    public GraphicableEmergency() {
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
    
    @Override
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public Polygon getPolygon() {
        return this.polygon;
    }

}
