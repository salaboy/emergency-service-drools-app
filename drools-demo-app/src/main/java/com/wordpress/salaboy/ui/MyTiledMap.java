/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import java.io.InputStream;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.Layer;
import org.newdawn.slick.tiled.TiledMap;

/**
 *
 * @author salaboy
 */
public class MyTiledMap extends TiledMap{

    public MyTiledMap(InputStream in, String tileSetsLocation) throws SlickException {
        super(in, tileSetsLocation);
    }

    public MyTiledMap(InputStream in) throws SlickException {
        super(in);
    }

    public MyTiledMap(String ref, String tileSetsLocation) throws SlickException {
        super(ref, tileSetsLocation);
    }

    public MyTiledMap(String ref, boolean loadTileSets) throws SlickException {
        super(ref, loadTileSets);
    }

    public MyTiledMap(String ref) throws SlickException {
        super(ref);
    }

    @Override
    protected void renderedLine(int visualY, int mapY, int layer) {
        
//        for(Object o : this.layers){
//            System.out.println("Layer -> "+((Layer)o).index);
//        }
// //       Layer layerObj = (Layer)this.layers.get(layerid);
//     //   System.out.println("Layer de max top ="+layerid +"layerobj = "+layerObj);
//        System.out.println("VisualY = "+visualY+ "- mapY = "+ mapY + "- layer = "+layer );
    }
    
}
