/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import com.wordpress.salaboy.model.FireTruck;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author salaboy
 */
public class GraphicableFireTruck extends GraphicableVehicle{
    private FireTruck fireTruck;
    private Animation animation;
    private Polygon   polygon;
    
    public GraphicableFireTruck(FireTruck ambulance) {
        this.fireTruck = ambulance;
    }

    public FireTruck getFireTruck() {
        return fireTruck;
    }

    public void setFireTruck(FireTruck ambulance) {
        this.fireTruck = ambulance;
    }

    @Override
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public Polygon getPolygon() {
        return this.polygon;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
    
    
    
        
    
}
