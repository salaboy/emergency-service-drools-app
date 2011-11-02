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
public interface Graphicable {
    public void setAnimation(Animation animation);
    public void setPolygon(Polygon polygon);
    public Animation getAnimation();
    public Polygon getPolygon();
}
