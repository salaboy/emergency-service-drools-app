/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import com.wordpress.salaboy.emergencyservice.worldui.slick.DELETE_MECityMapUI;
import com.wordpress.salaboy.emergencyservice.worldui.slick.WorldUI;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author esteban
 */
public class AnimationFactory {

    private static SpriteSheet ambulanceSprite;
    private static SpriteSheet genericEmergencySprite;
    private static SpriteSheet highlightedHospitalSprite;
    private static Animation ambulanceAnimation;
    private static Animation highlightedHospitalAnimation;
    private static Animation genericEmergencyAnimation;

    public static Animation getAmbulanceAnimation() {
        if (ambulanceAnimation == null) {
            ambulanceAnimation = new Animation();
            ambulanceAnimation.setLooping(false);
            ambulanceAnimation.setAutoUpdate(false);

            for (int row = 0; row < getAmbulanceSpriteSheet().getHorizontalCount(); row++) {
                for (int frame = 0; frame < getAmbulanceSpriteSheet().getVerticalCount(); frame++) {
                    ambulanceAnimation.addFrame(getAmbulanceSpriteSheet().getSprite(frame, row), 250);
                }
            }
        }
        return ambulanceAnimation;
    }

    public static Animation getHighlightedHospitalAnimation() {
        if (highlightedHospitalAnimation == null) {
            highlightedHospitalAnimation = new Animation();
            highlightedHospitalAnimation.setLooping(true);
            highlightedHospitalAnimation.setAutoUpdate(true);

            for (int row = 0; row < getHighlightedHospitalSpriteSheet().getHorizontalCount(); row++) {
                for (int frame = 0; frame < getHighlightedHospitalSpriteSheet().getVerticalCount(); frame++) {
                    highlightedHospitalAnimation.addFrame(getHighlightedHospitalSpriteSheet().getSprite(frame, row), 250);
                }
            }
        }
        return highlightedHospitalAnimation;
    }

    public static Animation getGenericEmergencyAnimation() {
        if (genericEmergencyAnimation == null) {
            genericEmergencyAnimation = new Animation();
            genericEmergencyAnimation.setLooping(true);
            genericEmergencyAnimation.setAutoUpdate(true);

            System.out.println("Emergency Sprite: horizontal= "+getGenericEmergencySpriteSheet().getHorizontalCount()+", vertical= "+getGenericEmergencySpriteSheet().getVerticalCount());
            
            for (int row = 0; row < getGenericEmergencySpriteSheet().getHorizontalCount(); row++) {
                for (int frame = 0; frame < getGenericEmergencySpriteSheet().getVerticalCount(); frame++) {
                    genericEmergencyAnimation.addFrame(getGenericEmergencySpriteSheet().getSprite(row, frame), 250);
                }
            }
        }
        return genericEmergencyAnimation;
    }

    public static Animation getEmergencyAnimation(EmergencyType emergencyType, int numberOfPeople) {
        Animation emergencyAnimation = new Animation();
        emergencyAnimation.setLooping(true);
        emergencyAnimation.setAutoUpdate(true);

        for (int row = 0; row < getGenericEmergencySpriteSheet().getHorizontalCount(); row++) {
            for (int frame = 0; frame < getGenericEmergencySpriteSheet().getVerticalCount(); frame++) {
                try {
                    Image sprite = getGenericEmergencySpriteSheet().getSprite(row, frame);
                    sprite.getGraphics().drawString("" + numberOfPeople, sprite.getWidth() / 2, sprite.getHeight() / 2);
                    emergencyAnimation.addFrame(sprite, 250);
                } catch (SlickException ex) {
                    Logger.getLogger(AnimationFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return emergencyAnimation;
    }

    private static SpriteSheet getAmbulanceSpriteSheet() {
        if (ambulanceSprite == null) {
            try {
                ambulanceSprite = new SpriteSheet("data/sprites/sprites-ambulancia.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ambulanceSprite;
    }

    private static SpriteSheet getHighlightedHospitalSpriteSheet() {
        if (highlightedHospitalSprite == null) {
            highlightedHospitalSprite = WorldUI.hospitalSheet;//new SpriteSheet("data/sprites/hospital-brillando.png", 64, 80, Color.magenta);
        }
        return highlightedHospitalSprite;
    }

    private static SpriteSheet getGenericEmergencySpriteSheet() {
        if (genericEmergencySprite == null) {
            try {
                genericEmergencySprite = new SpriteSheet("data/sprites/alert.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return genericEmergencySprite;
    }
}
