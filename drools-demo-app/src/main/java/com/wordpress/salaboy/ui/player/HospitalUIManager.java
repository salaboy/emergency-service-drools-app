/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui.player;

import com.wordpress.salaboy.CityEntitiesUtils;
import com.wordpress.salaboy.ui.CityMapUI;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.plugtree.training.model.Hospital;

/**
 *
 * @author salaboy
 */
public class HospitalUIManager {
    private static void addHospitalSelectedSituation(Hospital hospital) {
        Animation myHospital = new Animation();
        myHospital.setLooping(true);
        myHospital.setAutoUpdate(true);
        SpriteSheet mySheet = CityMapUI.hospitalSheet;

        for (int row = 0; row < mySheet.getHorizontalCount(); row++) {
            for (int frame = 0; frame < mySheet.getVerticalCount(); frame++) {
                myHospital.addFrame(mySheet.getSprite( row, frame), 250);
            }
        }

        Polygon myHospitalPolygon = new Polygon(new float[]{
                    Math.round(hospital.getPositionX()) * 16, Math.round(hospital.getPositionY()) * 16,
                    Math.round(hospital.getPositionX()) * 16 + 16, Math.round(hospital.getPositionY()) * 16,
                    Math.round(hospital.getPositionX()) * 16 + 16, Math.round(hospital.getPositionY()) * 16 + 16,
                    Math.round(hospital.getPositionX()) * 16, Math.round(hospital.getPositionY()) * 16 + 16
                });
        hospital.setAnimation(myHospital);
        hospital.setPolygon(myHospitalPolygon);
        

    }
    
    public static Hospital hightlightHospital(Long hospitalId){
        Hospital myHospital = CityEntitiesUtils.getHospitalById(hospitalId);
        addHospitalSelectedSituation(myHospital );
        return myHospital;
    }
}
