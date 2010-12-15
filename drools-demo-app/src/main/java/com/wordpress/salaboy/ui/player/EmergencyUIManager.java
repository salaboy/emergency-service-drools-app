/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui.player;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.ui.CityMapUI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.plugtree.training.model.Emergency;

/**
 *
 * @author salaboy
 */
public class EmergencyUIManager {
    
   
    
    
    
     private static void addEmergencySituation(Emergency emergency, SpriteSheet sheet, float positionX, float positionY) {
        
        Animation myEmergency = new Animation();
        myEmergency.setLooping(true);
        myEmergency.setAutoUpdate(true);
        SpriteSheet mySheet = sheet;
        for (int row = 0; row < mySheet.getHorizontalCount(); row++) {
            for (int frame = 0; frame < mySheet.getVerticalCount(); frame++) {
                myEmergency.addFrame(mySheet.getSprite(row, frame ), 250);
            }
        }

        Polygon myEmergencyPolygon = new Polygon(new float[]{
                    positionX, positionY,
                    positionX + 32, positionY,
                    positionX + 32, positionY + 32,
                    positionX, positionY + 32
                });
        emergency.setAnimation(myEmergency);
        emergency.setPolygon(myEmergencyPolygon);
       
        
    }
     
     public static Emergency addEmergencyAlert( float positionX, float positionY){
         Emergency emergency = new Emergency();
         addEmergencySituation(emergency, CityMapUI.alertSheet, positionX, positionY);
         return emergency;
     }
     
     
}
