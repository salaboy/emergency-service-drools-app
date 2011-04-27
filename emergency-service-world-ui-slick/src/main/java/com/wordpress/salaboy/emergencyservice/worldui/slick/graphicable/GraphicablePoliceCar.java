/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import com.wordpress.salaboy.model.PoliceCar;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author salaboy
 */
public class GraphicablePoliceCar implements Graphicable{
    private PoliceCar policeCar;
    private Animation animation;
    private Polygon   polygon;
    
    public GraphicablePoliceCar(PoliceCar policeCar) {
        this.policeCar = policeCar;
    }

    public PoliceCar getPoliceCar() {
        return policeCar;
    }

    public void setPoliceCar(PoliceCar policeCar) {
        this.policeCar = policeCar;
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
