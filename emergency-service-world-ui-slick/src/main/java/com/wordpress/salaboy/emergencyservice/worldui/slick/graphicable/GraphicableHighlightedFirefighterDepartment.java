/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import com.wordpress.salaboy.model.FirefightersDepartment;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author esteban
 */
public class GraphicableHighlightedFirefighterDepartment implements Graphicable {

    private FirefightersDepartment firefightersDepartment;
    private Animation animation;
    private Polygon polygon;

    public GraphicableHighlightedFirefighterDepartment(FirefightersDepartment firefightersDepartment) {
        this.firefightersDepartment = firefightersDepartment;
    }

    public FirefightersDepartment getFirefightersDepartment() {
        return firefightersDepartment;
    }

    public void setFirefightersDepartment(FirefightersDepartment firefightersDepartment) {
        this.firefightersDepartment = firefightersDepartment;
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
