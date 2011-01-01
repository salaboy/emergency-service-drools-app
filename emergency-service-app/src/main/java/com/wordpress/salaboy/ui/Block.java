/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author salaboy
 */
public class Block  {
	public Polygon poly;
	public Block(int x, int y, int test[],String type) {
        poly = new Polygon(new float[]{
				x+test[0], y+test[1],
				x+test[2], y+test[3],
				x+test[4], y+test[5],
				x+test[6], y+test[7],
        });   
	}
 
	public void update(int delta) {
	}
 
	public void draw(Graphics g) {
		g.draw(poly);
	}
}
