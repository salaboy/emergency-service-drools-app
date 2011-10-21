/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.model.*;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;
import com.wordpress.salaboy.model.Emergency.EmergencyType;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;

/**
 *
 * @author salaboy
 */
public class GraphicableFactory {

    private static int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private static int[] ys = new int[]{1, 7, 13, 19, 25};

    public static GraphicableVehicle newVehicle(Vehicle vehicle) {
        if (vehicle instanceof Ambulance) {
            return newAmbulance((Ambulance) vehicle);
        } else if (vehicle instanceof FireTruck) {
            return newFireTruck((FireTruck) vehicle);
        } else if (vehicle instanceof PoliceCar) {
            return newPoliceCar((PoliceCar) vehicle);
        }

        throw new IllegalArgumentException("Unknown Vehicle Type: " + vehicle.getClass().getName());
    }

    public static GraphicableAmbulance newAmbulance(Ambulance ambulance) {
        GraphicableAmbulance graphAmbulance = new GraphicableAmbulance(ambulance);

        Animation myAmbulance = AnimationFactory.getAmbulanceAnimation().copy();
        EmergencyEntityBuilding central = CityEntities.buildings.get("911");
        Polygon myPolygon = new Polygon(new float[]{
                    (central.getX() * 16), central.getY() * 16,
                    (central.getX() * 16) + 28, (central.getY() * 16),
                    (central.getX() * 16) + 28, (central.getY() * 16) + 28,
                    (central.getX() * 16), (central.getY() * 16) + 28
                });
        graphAmbulance.setAnimation(myAmbulance);
        graphAmbulance.setPolygon(myPolygon);

        return graphAmbulance;
    }

    public static GraphicableFireTruck newFireTruck(FireTruck fireTruck) {
        GraphicableFireTruck graphFireTruck = new GraphicableFireTruck(fireTruck);

        Animation myFireTruck = AnimationFactory.getFireTruckAnimation().copy();
        EmergencyEntityBuilding firefighters = CityEntities.buildings.get("Firefighters Department");
        Polygon myPolygon = new Polygon(new float[]{
                    firefighters.getX() * 16, firefighters.getY() * 16,
                    (firefighters.getX() * 16) + 28, (firefighters.getY() * 16),
                    (firefighters.getX() * 16) + 28, (firefighters.getY() * 16) + 28,
                    (firefighters.getX() * 16), (firefighters.getY() * 16) + 28
                });
        graphFireTruck.setAnimation(myFireTruck);
        graphFireTruck.setPolygon(myPolygon);

        return graphFireTruck;
    }

    public static GraphicablePoliceCar newPoliceCar(PoliceCar policeCar) {
        GraphicablePoliceCar graphPoliceCar = new GraphicablePoliceCar(policeCar);

        Animation myPoliceCar = AnimationFactory.getPoliceCarAnimation().copy();
        EmergencyEntityBuilding policeDepartment = CityEntities.buildings.get("Police Department");
        Polygon myPolygon = new Polygon(new float[]{
                    policeDepartment.getX() * 16, policeDepartment.getY() * 16,
                    (policeDepartment.getX() * 16) + 28, (policeDepartment.getY() * 16),
                    (policeDepartment.getX() * 16) + 28, (policeDepartment.getY() * 16) + 28,
                    (policeDepartment.getX() * 16), (policeDepartment.getY() * 16) + 28
                });
        graphPoliceCar.setAnimation(myPoliceCar);
        graphPoliceCar.setPolygon(myPolygon);

        return graphPoliceCar;
    }

    public static GraphicableHighlightedHospital newHighlightedHospital(Hospital hospital) {
        GraphicableHighlightedHospital graphHospital = new GraphicableHighlightedHospital(hospital);

        Animation myHospital = AnimationFactory.getHighlightedHospitalAnimation();

        Polygon myHospitalPolygon = new Polygon(new float[]{
                    Math.round(hospital.getX()) * 16, Math.round(hospital.getY()) * 16,
                    (Math.round(hospital.getX()) * 16) + 16, Math.round(hospital.getY()) * 16,
                    (Math.round(hospital.getX()) * 16) + 16, Math.round(hospital.getY()) * 16 + 16,
                    Math.round(hospital.getX()) * 16, Math.round(hospital.getY()) * 16 + 16
                });
        graphHospital.setAnimation(myHospital);
        graphHospital.setPolygon(myHospitalPolygon);

        return graphHospital;
    }

    public static GraphicableHighlightedFirefighterDepartment newHighlightedFirefighterDepartment(FirefightersDepartment firefightersDepartment) {
        GraphicableHighlightedFirefighterDepartment graphFirefightersDepartment = new GraphicableHighlightedFirefighterDepartment(firefightersDepartment);

        Animation myFireFighterDepartment = AnimationFactory.getHighlightedFirefighterDepartmentAnimation();

        Polygon myHospitalPolygon = new Polygon(new float[]{
                    Math.round(firefightersDepartment.getX()) * 16, Math.round(firefightersDepartment.getY()) * 16,
                    (Math.round(firefightersDepartment.getX()) * 16) + 16, Math.round(firefightersDepartment.getY()) * 16,
                    (Math.round(firefightersDepartment.getX()) * 16) + 16, Math.round(firefightersDepartment.getY()) * 16 + 16,
                    Math.round(firefightersDepartment.getX()) * 16, Math.round(firefightersDepartment.getY()) * 16 + 16
                });
        graphFirefightersDepartment.setAnimation(myFireFighterDepartment);
        graphFirefightersDepartment.setPolygon(myHospitalPolygon);

        return graphFirefightersDepartment;
    }

    public static GraphicableEmergency newGenericEmergency(Call call) {
        return newEmergency(call, EmergencyType.UNDEFINED, null);
    }

    public static GraphicableEmergency newEmergency(Call call, EmergencyType type, Integer numberOfPeople) {
        //TODO: fix this ???
        GraphicableEmergency graphEmergency = new GraphicableEmergency();

        Animation myEmergency = null;

        if (type == EmergencyType.UNDEFINED) {
            myEmergency = AnimationFactory.getGenericEmergencyAnimation();
        } else {
            myEmergency = AnimationFactory.getEmergencyAnimation(type, numberOfPeople);
        }

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

    public static GraphicableMenuBar newMenuBar() {
        
        GraphicableMenuBar graphMenuBar = new GraphicableMenuBar();
        Animation myMenuBar = AnimationFactory.getMenuBarAnimation();
        

        Polygon myMenuBarPolygon = new Polygon(new float[]{
                    0, 432,
                    640, 432,
                    640, 480,
                    0, 480
                });
        graphMenuBar.setAnimation(myMenuBar);
        graphMenuBar.setPolygon(myMenuBarPolygon);
        
        return graphMenuBar;
    }
    
    public static GraphicableEmergencyStatus newEmergencyStatus(String callId){
        GraphicableEmergencyStatus emergencyStatus = new GraphicableEmergencyStatus();
      
        
        Animation myEmergencyStatus = AnimationFactory.getEmergencyStatusAnimation(callId, 100);
        
        Polygon myEmergencyStatusPolygon = new Polygon(new float[]{
                    0 + 30 , 432 + 10,
                    0 + 30 + 143, 432 + 10,
                    0 + 30 + 143, 432 + 10 + 28,
                    0 + 30 , 432 + 10 + 28
                });
        emergencyStatus.setAnimation(myEmergencyStatus);
        emergencyStatus.setPolygon(myEmergencyStatusPolygon);
        
        return emergencyStatus;
    }
}
