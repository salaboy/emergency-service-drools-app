/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui.player;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.ui.CityMapUI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import com.wordpress.salaboy.model.Ambulance;

/**
 *
 * @author salaboy
 */
public class PlayerUIManager {
    
//    private float playerInitialX;
//    private float playerInitialY;
//
//    public PlayerUIManager(float playerInitialX, float playerInitialY) {
//        this.playerInitialX = playerInitialX;
//        this.playerInitialY = playerInitialY;
//    }
//    
//    
//    
//  
//
//    private void addPlayer(Ambulance ambulance, SpriteSheet sheet) {
//        Animation myPlayer = new Animation();
//        myPlayer.setLooping(false);
//        myPlayer.setAutoUpdate(false);
//        SpriteSheet mySheet = sheet;
//
//
//        for (int row = 0; row < mySheet.getHorizontalCount(); row++) {
//            for (int frame = 0; frame < mySheet.getVerticalCount(); frame++) {
//                myPlayer.addFrame(mySheet.getSprite(frame, row), 250);
//            }
//        }
//
//        Polygon myPolygon = new Polygon(new float[]{
//                    playerInitialX, playerInitialY,
//                    playerInitialX + 28, playerInitialY,
//                    playerInitialX + 28, playerInitialY + 28,
//                    playerInitialX, playerInitialY + 28
//                });
//        ambulance.setAnimation(myPlayer);
//        ambulance.setPolygon(myPolygon);
//        
//    }
//    
//    public Ambulance addAmbulance(Long ambulanceId){
//        Ambulance ambulance = CityEntitiesUtils.getAmbulanceById(ambulanceId);
//        try {
//            addPlayer(ambulance, new SpriteSheet("data/sprites/sprites-ambulancia.png", 32, 32, Color.magenta));
//        } catch (SlickException ex) {
//            Logger.getLogger(PlayerUIManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return ambulance;
//    }
}
