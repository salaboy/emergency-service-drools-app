/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;


import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import com.wordpress.salaboy.model.Hospital;

/**
 *
 * @author salaboy
 */
public class GraphicableFactory {
    
    private static int emergencyCentralX = 32;
    private static int emergencyCentralY = 400;
    private static int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private static int[] ys = new int[]{1, 7, 13, 19, 25};
    
    
    
    
    public static GraphicableAmbulance newAmbulance(Ambulance ambulance){
        GraphicableAmbulance graphAmbulance = new GraphicableAmbulance(ambulance);
        
        Animation myAmbulance = AnimationFactory.getAmbulanceAnimation();

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
        
        Animation myHospital = AnimationFactory.getHighlightedHospitalAnimation();

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
    
     public static GraphicableEmergency newEmergency(Call call){
         //TODO: fix this
        GraphicableEmergency graphEmergency = new GraphicableEmergency();
        
        Animation myEmergency = AnimationFactory.getGenericEmergencyAnimation();

        Polygon myEmergencyPolygon = new Polygon(new float[]{
                    xs[call.getX()] * 16, ys[call.getY()] * 16,
                    (xs[call.getX()] * 16) + 32, ys[call.getY()] * 16,
                    (xs[call.getX()] * 16) + 32, (ys[call.getY()] * 16) + 32,
                    xs[call.getX()] * 16, (ys[call.getY()] * 16) + 32
                });
        graphEmergency.setAnimation(myEmergency);
        graphEmergency.setPolygon(myEmergencyPolygon);
        
        graphEmergency.setCallId(call.getId());
        graphEmergency.setCallX(call.getX());
        graphEmergency.setCallY(call.getY());
        
        return graphEmergency;
    }

}
