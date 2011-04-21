/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;


import com.wordpress.salaboy.emergencyservice.worldui.slick.CityMapUI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;

/**
 *
 * @author salaboy
 */
public class GraphicableFactory {
    private static SpriteSheet ambulanceSprite;
    private static SpriteSheet emergencySprite;
    private static SpriteSheet highlightedHospitalSprite;
    private static int emergencyCentralX = 32;
    private static int emergencyCentralY = 400;
    private static int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private static int[] ys = new int[]{1, 7, 13, 19, 25};
    
    private static SpriteSheet getAmbulanceSpriteSheet(){
        if(ambulanceSprite == null){
            try {
                ambulanceSprite = new SpriteSheet("data/sprites/sprites-ambulancia.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ambulanceSprite;
    }
    private static SpriteSheet getHighlightedHospitalSpriteSheet(){
        if(highlightedHospitalSprite == null){
            
                highlightedHospitalSprite = CityMapUI.hospitalSheet;//new SpriteSheet("data/sprites/hospital-brillando.png", 64, 80, Color.magenta);
            
        }
        return highlightedHospitalSprite;
    }
    private static SpriteSheet getEmergencySpriteSheet(){
        if(emergencySprite == null){
            try {
                emergencySprite = new SpriteSheet("data/sprites/alert.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return emergencySprite;
    }
    
    
    public static GraphicableAmbulance newAmbulance(Ambulance ambulance){
        GraphicableAmbulance graphAmbulance = new GraphicableAmbulance(ambulance);
        
        Animation myAmbulance = new Animation();
        myAmbulance.setLooping(false);
        myAmbulance.setAutoUpdate(false);
        


        for (int row = 0; row < getAmbulanceSpriteSheet().getHorizontalCount(); row++) {
            for (int frame = 0; frame < getAmbulanceSpriteSheet().getVerticalCount(); frame++) {
                myAmbulance.addFrame(getAmbulanceSpriteSheet().getSprite(frame, row), 250);
            }
        }

        Polygon myPolygon = new Polygon(new float[]{
                    emergencyCentralX, emergencyCentralY,
                    emergencyCentralX + 28, emergencyCentralY,
                    emergencyCentralX + 28, emergencyCentralY + 28,
                    emergencyCentralX, emergencyCentralY + 28
                });
        graphAmbulance.setAnimation(myAmbulance);
        graphAmbulance.setPolygon(myPolygon);
        
        return graphAmbulance;
    }
    
    public static GraphicableHighlightedHospital newHighlightedHospital(Hospital hospital){
        GraphicableHighlightedHospital graphHospital = new GraphicableHighlightedHospital(hospital);
        
        Animation myHospital = new Animation();
        myHospital.setLooping(true);
        myHospital.setAutoUpdate(true);
        


        for (int row = 0; row < getHighlightedHospitalSpriteSheet().getHorizontalCount(); row++) {
            for (int frame = 0; frame < getHighlightedHospitalSpriteSheet().getVerticalCount(); frame++) {
                myHospital.addFrame(getHighlightedHospitalSpriteSheet().getSprite( row, frame), 250);
            }
        }

        Polygon myHospitalPolygon = new Polygon(new float[]{
                    Math.round(hospital.getPositionX()) * 16, Math.round(hospital.getPositionY()) * 16,
                    Math.round(hospital.getPositionX()) * 16 + 16, Math.round(hospital.getPositionY()) * 16,
                    Math.round(hospital.getPositionX()) * 16 + 16, Math.round(hospital.getPositionY()) * 16 + 16,
                    Math.round(hospital.getPositionX()) * 16, Math.round(hospital.getPositionY()) * 16 + 16
                });
        graphHospital.setAnimation(myHospital);
        graphHospital.setPolygon(myHospitalPolygon);
        
        return graphHospital;
    }
    
     public static GraphicableEmergency newEmergency(Emergency emergency){
        GraphicableEmergency graphEmergency = new GraphicableEmergency(emergency);
        
        Animation myEmergency = new Animation();
        myEmergency.setLooping(true);
        myEmergency.setAutoUpdate(true);
        


        for (int row = 0; row < getEmergencySpriteSheet().getHorizontalCount(); row++) {
            for (int frame = 0; frame < getEmergencySpriteSheet().getVerticalCount(); frame++) {
                myEmergency.addFrame(getEmergencySpriteSheet().getSprite( row, frame), 250);
            }
        }

        Polygon myEmergencyPolygon = new Polygon(new float[]{
                    xs[emergency.getCall().getX()] * 16, ys[emergency.getCall().getY()] * 16,
                    (xs[emergency.getCall().getX()] * 16) + 32, ys[emergency.getCall().getY()] * 16,
                    (xs[emergency.getCall().getX()] * 16) + 32, (ys[emergency.getCall().getY()] * 16) + 32,
                    xs[emergency.getCall().getX()] * 16, (ys[emergency.getCall().getY()] * 16) + 32
                });
        graphEmergency.setAnimation(myEmergency);
        graphEmergency.setPolygon(myEmergencyPolygon);
        
        return graphEmergency;
    }

}
