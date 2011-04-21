/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;
import com.wordpress.salaboy.model.Emergency;

/**
 *
 * @author salaboy
 */
public class GraphicableEmergency implements Graphicable{
    private Emergency emergency;
    private Polygon polygon;
    private Animation animation;
    
    public GraphicableEmergency(Emergency emergency) {
        this.emergency = emergency;
    }

    public Emergency getEmergency() {
        return emergency;
    }

    public void setEmergency(Emergency emergency) {
        this.emergency = emergency;
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
