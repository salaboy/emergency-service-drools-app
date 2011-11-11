/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model;

import com.wordpress.salaboy.model.buildings.Hospital;
import com.wordpress.salaboy.model.buildings.EmergencyServiceCentral;
import com.wordpress.salaboy.model.buildings.EntityBuilding;
import com.wordpress.salaboy.model.buildings.FirefightersDepartment;
import com.wordpress.salaboy.model.buildings.PoliceDepartment;
import com.wordpress.salaboy.model.vehicles.Vehicle;
import com.wordpress.salaboy.model.vehicles.Ambulance;
import com.wordpress.salaboy.model.vehicles.FireTruck;
import com.wordpress.salaboy.model.roles.Doctor.DoctorSpeciality;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import com.wordpress.salaboy.model.roles.Doctor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class CityEntities {

    public static final List<Vehicle> vehicles = new ArrayList<Vehicle>() {

        {
            add(initializeHeartAttackAmbulance());
            add(initializeFireAmbulance());
            add(initializeCarCrashAmbulance());
            add(initializeBigFireTruck());
            add(initializeMediumFireTruck());
            add(initializeSmallFireTruck());
        }
    };
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

    public static Ambulance getAmbulanceById(String id) {
        for (Map.Entry<EmergencyType, List<Ambulance>> entry : ambulances.entrySet()) {
            for (Ambulance ambulance : entry.getValue()) {
                if (ambulance.getId().equals(id)) {
                    return ambulance;
                }
            }
        }
        return null;
    }

    public static Hospital getHospitalById(Long id) {
        for (Hospital hospital : hospitals) {
            if (hospital.getId().equals(id)) {
                return hospital;
            }
        }
        return null;
    }
    public static final Map<DoctorSpeciality, List<Doctor>> doctors = new HashMap<DoctorSpeciality, List<Doctor>>() {

        {
            put(DoctorSpeciality.BURNS, new ArrayList<Doctor>() {

                {
                    add(new Doctor("Burns Doctor",DoctorSpeciality.BURNS));
                }
            });
            put(DoctorSpeciality.BONES, new ArrayList<Doctor>() {

                {
                    add(new Doctor("Bones Doctor",DoctorSpeciality.BONES));
                }
            });
            put(DoctorSpeciality.REANIMATION, new ArrayList<Doctor>() {

                {
                    add(new Doctor("Reanimation Doctor", DoctorSpeciality.REANIMATION));
                }
            });
        }
    };
    public static final List< Hospital> hospitals = new ArrayList<Hospital>() {

        {

            add(new Hospital("Hospital 01", 11, 13));
            add(new Hospital("Hospital 02", 35, 13));
            add(new Hospital("Hospital 03", 17, 25));


        }
    };
    public static final Map<String, EntityBuilding> buildings = new HashMap<String, EntityBuilding>() {

        {
            put("911", new EmergencyServiceCentral("911", 2, 25));
            put("Police Department", new PoliceDepartment("Police Department", 15, 7));
            put("Firefighters Department", new FirefightersDepartment("Firefighters Department", 35, 25));
        }
    };

    public static Hospital getHospitalByCoordinates(float x, float y) {

        float newx = Math.round(x / 16);
        float newy = Math.round(x / 16);

        for (Hospital thishospital : CityEntities.hospitals) {
            if (thishospital.getX() == newx && thishospital.getY() == newy) {
                return thishospital;
            }
        }
        return null;
    }

    public static String translatePosition(int x, int y) {
        String xString = "";
        String yString = "";


        switch (x) {

            case 2:
                xString = "1st Street";
                break;

            case 7:
                xString = "2nd Street";
                break;

            case 13:
                xString = "3rd Street";
                break;

            case 19:
                xString = "4th Street";
                break;

            case 25:
                xString = "5th Street";
                break;

            case 31:
                xString = "6th Street";
                break;

            case 37:
                xString = "7th Street";
                break;
        }
        switch (y) {

            case 2:
                yString = "A Street";
                break;

            case 7:
                yString = "B Street";
                break;

            case 13:
                yString = "C Street";
                break;

            case 19:
                yString = "D Street";
                break;

            case 25:
                yString = "E Street";
                break;

        }

        if (xString.equals("") || yString.equals("")) {
            return "N/A";
        }

        return "" + xString + " and " + yString;
    }

    private static Ambulance initializeFireAmbulance() {

        Ambulance fireAmbulance = new Ambulance("Fire Ambulance");
        return fireAmbulance;
    }

    private static Ambulance initializeHeartAttackAmbulance() {

        Ambulance heartAttackAmbulance = new Ambulance("Strokes Ambulance");
        return heartAttackAmbulance;
    }

    private static Ambulance initializeCarCrashAmbulance() {

        Ambulance carCrashAmbulance = new Ambulance("Fire & Bones Ambulance");
        return carCrashAmbulance;
    }

    private static FireTruck initializeBigFireTruck() {

        FireTruck bigFireTruck = new FireTruck("Big Fire Truck");
        bigFireTruck.setTankSize(10);
        bigFireTruck.setTankLevel(10);
        return bigFireTruck;
    }

    private static FireTruck initializeMediumFireTruck() {

        FireTruck mediumFireTruck = new FireTruck("Medium Fire Truck");
        mediumFireTruck.setTankSize(5);
        mediumFireTruck.setTankLevel(5);
        return mediumFireTruck;
    }

    private static FireTruck initializeSmallFireTruck() {

        FireTruck smallFireTruck = new FireTruck("Small Fire Truck");
        smallFireTruck.setTankSize(2);
        smallFireTruck.setTankLevel(2);
        return smallFireTruck;
    }

    public static Ambulance getAmbulanceById(EmergencyType type, String id) {

        for (Ambulance ambulancenow : CityEntities.ambulances.get(type)) {
            if (ambulancenow.getId().equals(id)) {
                return ambulancenow;
            }
        }
        return null;
    }
}
