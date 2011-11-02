/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author salaboy
 */
public class GraphicableMenuBar implements Graphicable {

    private Animation animation;
    private Polygon polygon;

    public GraphicableMenuBar() {
    }
    
    

    @Override
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
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
