/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy;

import com.wordpress.salaboy.graphicable.GraphicableAmbulance;
import com.wordpress.salaboy.graphicable.GraphicableFactory;
import com.wordpress.salaboy.util.MedicalKitUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Medic;
import com.wordpress.salaboy.model.Medic.MedicSpeciality;
import com.wordpress.salaboy.model.MedicalKit;

/**
 *
 * @author salaboy
 */
public class CityEntitiesUtils {
    
    public static final Map<EmergencyType, List<Ambulance>> ambulances = new HashMap<EmergencyType, List<Ambulance>>() {

        {
            put(EmergencyType.FIRE, new ArrayList<Ambulance>() {

                {
                    add(initializeFireAmbulance());
                }
            });
            put(EmergencyType.HEART_ATTACK, new ArrayList<Ambulance>() {

                {
                    add(initializeHeartAttackAmbulance());
                }
            });
            put(EmergencyType.CAR_CRASH, new ArrayList<Ambulance>() {

                {
                    add(initializeCarCrashAmbulance());
                }
            });
        }
    };
    
   
//    public static GraphicableAmbulance getGraphicableAmbulanceById(Long id){
//        for (Map.Entry<EmergencyType, List<GraphicableAmbulance>> entry : ambulances.entrySet()) {
//            for (GraphicableAmbulance ambulance : entry.getValue()) {
//                if (ambulance.getAmbulance().getId().compareTo(id) == 0){
//                    return ambulance;
//                }
//            }
//        }
//        return null;
//    }
    
    public static Ambulance getAmbulanceById(Long id){
        for (Map.Entry<EmergencyType, List<Ambulance>> entry : ambulances.entrySet()) {
            for (Ambulance ambulance : entry.getValue()) {
                if (ambulance.getId().compareTo(id) == 0){
                    return ambulance;
                }
            }
        }
        return null;
    }
    
    public static Hospital getHospitalById(Long id){
        for (Map.Entry<String, Hospital> entry : hospitals.entrySet()) {
            if (entry.getValue().getId().compareTo(id) == 0){
                return entry.getValue();
            }
        }
        return null;
    }
    
    public static final Map<MedicSpeciality, List<Medic>> doctors = new HashMap<MedicSpeciality, List<Medic>>() {

        {
            put(MedicSpeciality.BURNS, new ArrayList<Medic>() {

                {
                    add(new Medic(MedicSpeciality.BURNS));
                }
            });
            put(MedicSpeciality.BONES, new ArrayList<Medic>() {

                {
                    add(new Medic(MedicSpeciality.BONES));
                }
            });
            put(MedicSpeciality.REANIMATION, new ArrayList<Medic>() {

                {
                    add(new Medic(MedicSpeciality.REANIMATION));
                }
            });
        }
    };
    
    public static final Map<String, Hospital> hospitals = new HashMap<String, Hospital>(){{
        
        put("Hosital 01", new Hospital("Hospital 01", 11, 13));
        put("Hosital 02", new Hospital("Hospital 02", 35, 13));
        put("Hosital 03", new Hospital("Hospital 03", 17, 25));
       
    
    }};
    public static Hospital getHospitalByCoordinates(float x, float y) {
        Collection<Hospital> myhospitals = CityEntitiesUtils.hospitals.values();
        float newx = Math.round(x/16);
        float newy = Math.round(x/16);
        
        for(Hospital thishospital : myhospitals){
            if(thishospital.getPositionX() == newx && thishospital.getPositionY() == newy){
                return thishospital;
            }
        }
        return null;
    }
     
      public static String translatePosition(int x, int y){
        String xString = "";
        String yString = "";
        

        switch(x){
            
            case 2: xString = "1st Street"; break;
            
            case 7: xString = "2nd Street"; break;
            
            case 13: xString = "3rd Street"; break; 
            
            case 19: xString = "4th Street"; break;
            
            case 25: xString = "5th Street"; break;
            
            case 31: xString = "6th Street"; break;
            
            case 37: xString = "7th Street"; break;      
        }
         switch(y){
            
            case 2: yString = "A Street"; break;
            
            case 7: yString = "B Street"; break;
            
            case 13: yString = "C Street"; break;
            
            case 19: yString = "D Street"; break;
            
            case 25: yString = "E Street"; break;
            
        }
        
        if(xString.equals("") || yString.equals("")){
            return "N/A";
        }
        
        return ""+xString +" and "+yString;
    }
      
      private static Ambulance initializeFireAmbulance() {
        MedicalKit fireKit = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.BURNS);
        Ambulance fireAmbulance = new Ambulance("Fire Ambulance");
        fireAmbulance.addKit(fireKit);
        return fireAmbulance;
    }

    private static Ambulance initializeHeartAttackAmbulance() {
        MedicalKit heartAttackKit = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.REANIMATION);
        Ambulance heartAttackAmbulance = new Ambulance("Strokes Ambulance");
        heartAttackAmbulance.addKit(heartAttackKit);
        return heartAttackAmbulance;
    }

    private static Ambulance initializeCarCrashAmbulance() {
        MedicalKit carCrashKit1 = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.BONES);
        MedicalKit carCrashKit2 = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.BURNS);
        Ambulance carCrashAmbulance = new Ambulance("Fire & Bones Ambulance");
        carCrashAmbulance.addKit(carCrashKit1);
        carCrashAmbulance.addKit(carCrashKit2);
        return carCrashAmbulance;
    }
    
     public static Ambulance getAmbulanceById(EmergencyType type, Long id) {
        
        for (Ambulance ambulancenow : CityEntitiesUtils.ambulances.get(type)) {
            if (ambulancenow.getId().compareTo(id) == 0) {
                return ambulancenow;
            }
        }
        return null;
    }
     
//    public static GraphicableAmbulance getGraphicableAmbulanceById(EmergencyType type, Long id) {
//         
//        for (GraphicableAmbulance ambulancenow : CityEntitiesUtils.ambulances.get(type)) {
//            if (ambulancenow.getAmbulance().getId().compareTo(id) == 0) {
//                return ambulancenow;
//            }
//        }
//        return null;
//    }
    
}
