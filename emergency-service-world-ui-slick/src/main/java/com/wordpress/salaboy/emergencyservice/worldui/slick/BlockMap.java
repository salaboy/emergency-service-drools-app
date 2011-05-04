/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick;

import java.util.ArrayList;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author salaboy
 */
public class BlockMap {
	public static TiledMap tmap;
	public static int mapWidth;
	public static int mapHeight;
	private int square[] = {1,1,15,1,15,15,1,15}; //square shaped tile
	public static ArrayList<Object> entities;
        public static ArrayList<Object> emergencies = new ArrayList<Object>();
        public static ArrayList<Object> hospitals = new ArrayList<Object>();
        public static ArrayList<Object> corners = new ArrayList<Object>();
        
 
	public BlockMap(String ref) throws SlickException {
		entities = new ArrayList<Object>();
		tmap = new TiledMap(ref, "data");
                
		mapWidth = tmap.getWidth() * tmap.getTileWidth();
		mapHeight = tmap.getHeight() * tmap.getTileHeight();
 
		for (int x = 0; x < tmap.getWidth(); x++) {
			for (int y = 0; y < tmap.getHeight(); y++) {
				int tileID = tmap.getTileId(x, y, 0);
				if (tileID == 1) {
					entities.add(
                                        new Block(x * 16, y * 16, square, "square")
                                        );
				}
			}
		}
	}

    public void initializeCorners() {
        int[] xs = new int[]{1, 7, 13,  19,  25,  31,  37};
        int[] ys = new int[]{1, 7, 13,  19,  25};
        for (int x = 0; x < xs.length ; x++) {
			for (int y = 0; y < ys.length ; y++) {
                            corners.add(new Block(xs[x] * 16, ys[y] * 16, square, "square"));
                        }
        } 
        
    }
    
        
}
