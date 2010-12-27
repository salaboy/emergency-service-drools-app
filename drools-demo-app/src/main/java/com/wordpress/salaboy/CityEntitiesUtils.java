/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy;

import com.wordpress.salaboy.util.MedicalKitUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Doctor;
import com.wordpress.salaboy.model.Doctor.DoctorSpeciality;
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
    
    public static final Map<DoctorSpeciality, List<Doctor>> doctors = new HashMap<DoctorSpeciality, List<Doctor>>() {

        {
            put(DoctorSpeciality.BURNS, new ArrayList<Doctor>() {

                {
                    add(new Doctor(DoctorSpeciality.BURNS));
                }
            });
            put(DoctorSpeciality.BONES, new ArrayList<Doctor>() {

                {
                    add(new Doctor(DoctorSpeciality.BONES));
                }
            });
            put(DoctorSpeciality.REANIMATION, new ArrayList<Doctor>() {

                {
                    add(new Doctor(DoctorSpeciality.REANIMATION));
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
        MedicalKit fireKit = MedicalKitUtil.createNewMEdicalKit(DoctorSpeciality.BURNS);
        Ambulance fireAmbulance = new Ambulance("Fire Ambulance");
        fireAmbulance.addKit(fireKit);
        return fireAmbulance;
    }

    private static Ambulance initializeHeartAttackAmbulance() {
        MedicalKit heartAttackKit = MedicalKitUtil.createNewMEdicalKit(DoctorSpeciality.REANIMATION);
        Ambulance heartAttackAmbulance = new Ambulance("Strokes Ambulance");
        heartAttackAmbulance.addKit(heartAttackKit);
        return heartAttackAmbulance;
    }

    private static Ambulance initializeCarCrashAmbulance() {
        MedicalKit carCrashKit1 = MedicalKitUtil.createNewMEdicalKit(DoctorSpeciality.BONES);
        MedicalKit carCrashKit2 = MedicalKitUtil.createNewMEdicalKit(DoctorSpeciality.BURNS);
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
