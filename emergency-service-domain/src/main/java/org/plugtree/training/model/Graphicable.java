/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.plugtree.training.model;

import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author salaboy
 */
public interface Graphicable {
    public Animation getAnimation();
    public Polygon getPolygon();
}
