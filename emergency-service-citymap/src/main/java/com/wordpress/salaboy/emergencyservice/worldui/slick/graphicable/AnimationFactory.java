/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import com.wordpress.salaboy.model.persistence.PersistenceServiceProvider;
import java.util.EnumMap;
import java.util.Map;
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

    private static Map<EmergencyType, SpriteSheet> emergencySprites = new EnumMap<EmergencyType, SpriteSheet>(EmergencyType.class);
    private static SpriteSheet ambulanceSprite;
    private static SpriteSheet fireTruckSprite;
    private static SpriteSheet fireTruckGrayedSprite;
    private static SpriteSheet policeCarSprite;
    private static SpriteSheet highlightedHospitalSprite;
    private static SpriteSheet highlightedFireFighterDepartmentSprite;
    private static SpriteSheet glowSprites;
    private static SpriteSheet menuBarSprite;
    private static SpriteSheet fireEmergencyStatusSprite;
    private static SpriteSheet heartAttackEmergencyStatusSprite;
    private static Animation ambulanceAnimation;
    private static Animation fireTruckAnimation;
    private static Animation fireTruckGrayedAnimation;
    private static Animation policeCarAnimation;
    private static Animation highlightedHospitalAnimation;
    private static Animation highlightedFireFighterDepartmentAnimation;
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
                    highlightedHospitalAnimation.addFrame(getHighlightedHospitalSpriteSheet().getSprite(row, frame), 250);
                }
            }
        }
        return highlightedHospitalAnimation;
    }

    public static Animation getHighlightedFirefighterDepartmentAnimation() {
        if (highlightedFireFighterDepartmentAnimation == null) {
            highlightedFireFighterDepartmentAnimation = new Animation();
            highlightedFireFighterDepartmentAnimation.setLooping(true);
            highlightedFireFighterDepartmentAnimation.setAutoUpdate(true);

            for (int row = 0; row < getHighlightedFirefighterDepartmentSpriteSheet().getHorizontalCount(); row++) {
                for (int frame = 0; frame < getHighlightedFirefighterDepartmentSpriteSheet().getVerticalCount(); frame++) {
                    highlightedFireFighterDepartmentAnimation.addFrame(getHighlightedFirefighterDepartmentSpriteSheet().getSprite(row, frame), 250);
                }
            }
        }
        return highlightedFireFighterDepartmentAnimation;
    }

    public static Animation getGenericEmergencyAnimation() {
        if (genericEmergencyAnimation == null) {
            genericEmergencyAnimation = getEmergencyAnimation(EmergencyType.UNDEFINED, null);
        }
        return genericEmergencyAnimation;
    }

    public static Animation getEmergencyAnimation(EmergencyType emergencyType, Integer numberOfPeople) {
        Animation emergencyAnimation = new Animation();
        emergencyAnimation.setLooping(true);
        emergencyAnimation.setAutoUpdate(true);

        for (int row = 0; row < getEmergencySpriteSheet(emergencyType).getHorizontalCount(); row++) {
            for (int frame = 0; frame < getEmergencySpriteSheet(emergencyType).getVerticalCount(); frame++) {
                try {
                    Image sprite = getEmergencySpriteSheet(emergencyType).getSprite(row, frame);
                    if (numberOfPeople != null) {
                        sprite.getGraphics().drawString("" + numberOfPeople, sprite.getWidth() / 2, sprite.getHeight() / 2);
                    }
                    emergencyAnimation.addFrame(sprite, 250);
                } catch (SlickException ex) {
                    Logger.getLogger(AnimationFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return emergencyAnimation;
    }

    public static Animation getMenuBarAnimation() {
        Animation menuBarAnimation = new Animation();
      
        menuBarAnimation.addFrame(getMenuBarSpriteSheet().getSprite(0, 0), 1000);

        return menuBarAnimation;
    }

    public static Animation getEmergencyStatusAnimation( EmergencyType type, int total, int remaining) {
        Animation emergencyStatusAnimation = new Animation();
        emergencyStatusAnimation.setLooping(true);
        emergencyStatusAnimation.setAutoUpdate(true);
        if (type == EmergencyType.FIRE) {
            String percentage = calculatePercentage(total, remaining);
            emergencyStatusAnimation.addFrame(getFireEmergencyStatusSpriteSheet(percentage).getSprite(0, 0), 1000);
        }
        if (type == EmergencyType.HEART_ATTACK) {
            String percentage = calculatePercentage(total, remaining);
            emergencyStatusAnimation.addFrame(getHeartAttackEmergencyStatusSpriteSheet(percentage).getSprite(0, 0), 1000);
        }
        return emergencyStatusAnimation;
    }

    public static SpriteSheet getAmbulanceSpriteSheet() {
        if (ambulanceSprite == null) {
            try {
                ambulanceSprite = new SpriteSheet("data/sprites/ambulance.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ambulanceSprite;
    }

    public static SpriteSheet getFireTruckSpriteSheet() {
        if (fireTruckSprite == null) {
            try {
                fireTruckSprite = new SpriteSheet("data/sprites/fireTruck.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fireTruckSprite;
    }

    public static SpriteSheet getGrayedFireTruckSpriteSheet() {
        if (fireTruckGrayedSprite == null) {
            try {
                fireTruckGrayedSprite = new SpriteSheet("data/sprites/sprites-bomberos-weak.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fireTruckGrayedSprite;
    }

    public static SpriteSheet getPoliceCarSpriteSheet() {
        if (policeCarSprite == null) {
            try {
                policeCarSprite = new SpriteSheet("data/sprites/policeCar.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return policeCarSprite;
    }

    private static SpriteSheet getHighlightedHospitalSpriteSheet() {
        if (highlightedHospitalSprite == null) {
            try {
                highlightedHospitalSprite = new SpriteSheet("data/sprites/hospital-brillando.png", 64, 80, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(AnimationFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return highlightedHospitalSprite;
    }

    private static SpriteSheet getHighlightedFirefighterDepartmentSpriteSheet() {
        if (highlightedFireFighterDepartmentSprite == null) {
            try {
                highlightedFireFighterDepartmentSprite = new SpriteSheet("data/sprites/bombero-brillando.png", 64, 80, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(AnimationFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return highlightedFireFighterDepartmentSprite;
    }

    private static SpriteSheet getFireEmergencyStatusSpriteSheet(String percentage) {

        try {
            fireEmergencyStatusSprite = new SpriteSheet("data/sprites/fire-" + percentage + ".png", 143, 28, Color.magenta);
        } catch (SlickException ex) {
            Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fireEmergencyStatusSprite;
    }
    private static SpriteSheet getHeartAttackEmergencyStatusSpriteSheet(String percentage) {

        try {
            if(!percentage.equals("00")){
                heartAttackEmergencyStatusSprite = new SpriteSheet("data/sprites/people-x.png", 78, 28, Color.magenta);
            } else{
                heartAttackEmergencyStatusSprite = new SpriteSheet("data/sprites/people-ok.png", 78, 28, Color.magenta);
            }
            
        } catch (SlickException ex) {
            Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return heartAttackEmergencyStatusSprite;
    }

    private static SpriteSheet getMenuBarSpriteSheet() {

        try {
            menuBarSprite = new SpriteSheet("data/sprites/menu-bar.png", 640, 48, Color.magenta);
        } catch (SlickException ex) {
            Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return menuBarSprite;
    }

    private static SpriteSheet getEmergencySpriteSheet(EmergencyType type) {
        if (!emergencySprites.containsKey(type)) {
            try {
                emergencySprites.put(type, new SpriteSheet("data/sprites/alert-" + type.name() + ".png", 32, 32, Color.magenta));
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return emergencySprites.get(type);
    }

    private static SpriteSheet getGlowSpriteSheet() {
        if (glowSprites == null) {
            try {
                glowSprites = new SpriteSheet("data/sprites/glow.png", 32, 32, Color.magenta);
            } catch (SlickException ex) {
                Logger.getLogger(GraphicableFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return glowSprites;
    }

    public static Animation getFireTruckAnimation() {
        if (fireTruckAnimation == null) {
            fireTruckAnimation = new Animation();
            fireTruckAnimation.setLooping(false);
            fireTruckAnimation.setAutoUpdate(false);

            for (int row = 0; row < getFireTruckSpriteSheet().getHorizontalCount(); row++) {
                for (int frame = 0; frame < getFireTruckSpriteSheet().getVerticalCount(); frame++) {
                    fireTruckAnimation.addFrame(getFireTruckSpriteSheet().getSprite(frame, row), 250);
                }
            }
        }
        return fireTruckAnimation;
    }
    
    public static Animation getFireTruckGrayedAnimation() {
        if (fireTruckGrayedAnimation == null) {
            fireTruckGrayedAnimation = new Animation();
            fireTruckGrayedAnimation.setLooping(false);
            fireTruckGrayedAnimation.setAutoUpdate(false);

            for (int row = 0; row < getGrayedFireTruckSpriteSheet().getHorizontalCount(); row++) {
                for (int frame = 0; frame < getGrayedFireTruckSpriteSheet().getVerticalCount(); frame++) {
                    fireTruckGrayedAnimation.addFrame(getGrayedFireTruckSpriteSheet().getSprite(frame, row), 250);
                }
            }
        }
        return fireTruckGrayedAnimation;
    }

    static Animation getPoliceCarAnimation() {
        if (policeCarAnimation == null) {
            policeCarAnimation = new Animation();
            policeCarAnimation.setLooping(false);
            policeCarAnimation.setAutoUpdate(false);

            for (int row = 0; row < getPoliceCarSpriteSheet().getHorizontalCount(); row++) {
                for (int frame = 0; frame < getPoliceCarSpriteSheet().getVerticalCount(); frame++) {
                    policeCarAnimation.addFrame(getPoliceCarSpriteSheet().getSprite(frame, row), 250);
                }
            }
        }
        return policeCarAnimation;
    }

    public static Animation addGlow(Graphicable graphicable) {
        Animation glowAnimation = new Animation();
        glowAnimation.setLooping(false);
        glowAnimation.setAutoUpdate(true);

        for (int row = 0; row < getGlowSpriteSheet().getHorizontalCount(); row++) {
            for (int frame = 0; frame < getGlowSpriteSheet().getVerticalCount(); frame++) {
                try {
                    Image sprite = getGlowSpriteSheet().getSprite(row, frame);
                    sprite.getGraphics().drawImage(graphicable.getAnimation().getCurrentFrame(), 0, 0);
                    glowAnimation.addFrame(sprite, 250);
                } catch (SlickException ex) {
                    Logger.getLogger(AnimationFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return glowAnimation;
    }

    private static synchronized String calculatePercentage(int total, int remaining) {
        System.out.println("Total =" + total);
        System.out.println("Remaining =" + remaining);
        float percentage = (100 / total) * remaining;
        System.out.println("Percentage = " + percentage);
        if (percentage == 100) {
            return "100";
        }
        if (percentage < 100 && percentage >= 90) {
            return "90";
        }

        if (percentage < 90 && percentage >= 80) {
            return "80";
        }
        if (percentage < 80 && percentage >= 70) {
            return "70";
        }

        if (percentage < 70 && percentage >= 60) {
            return "60";
        }

        if (percentage < 60 && percentage >= 50) {
            return "50";
        }

        if (percentage < 50 && percentage >= 40) {
            return "40";
        }

        if (percentage < 40 && percentage >= 30) {
            return "30";
        }

        if (percentage < 30 && percentage >= 20) {
            return "20";
        }

        if (percentage < 20 && percentage > 1) {
            return "10";
        }

        return "00";
    }
}
