/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;


import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.CityEntities;
import com.wordpress.salaboy.model.EmergencyEntityBuilding;
import com.wordpress.salaboy.model.FireTruck;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.PoliceCar;
import com.wordpress.salaboy.model.Vehicle;

/**
 *
 * @author salaboy
 */
public class GraphicableFactory {
    
    
    private static int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private static int[] ys = new int[]{1, 7, 13, 19, 25};
    
    
    
    public static Graphicable newVehicle(Vehicle vehicle){
        if (vehicle instanceof Ambulance){
            return newAmbulance((Ambulance)vehicle);
        } else if (vehicle instanceof FireTruck){
            return newFireTruck((FireTruck)vehicle);
        } else if (vehicle instanceof PoliceCar){
            return newPoliceCar((PoliceCar)vehicle);
        }
        
        throw new IllegalArgumentException("Unknown Vehicle Type: "+vehicle.getClass().getName());
    }
    
    
    public static GraphicableAmbulance newAmbulance(Ambulance ambulance){
        GraphicableAmbulance graphAmbulance = new GraphicableAmbulance(ambulance);
        
        Animation myAmbulance = AnimationFactory.getAmbulanceAnimation();
        EmergencyEntityBuilding central = CityEntities.buildings.get("911");
        Polygon myPolygon = new Polygon(new float[]{
                    central.getX() * 16, central.getY() * 16,
                    (central.getX() * 16) + 28,(central.getY() * 16),
                    (central.getX() * 16) + 28, (central.getY() * 16) + 28,
                    (central.getX() * 16), (central.getY() * 16) + 28
                });
        graphAmbulance.setAnimation(myAmbulance);
        graphAmbulance.setPolygon(myPolygon);
        
        return graphAmbulance;
    }
    
    public static GraphicableFireTruck newFireTruck(FireTruck fireTruck){
        GraphicableFireTruck graphFireTruck = new GraphicableFireTruck(fireTruck);
        
        Animation myFireTruck = AnimationFactory.getFireTruckAnimation();
        EmergencyEntityBuilding firefighters = CityEntities.buildings.get("Firefighters Department");
        Polygon myPolygon = new Polygon(new float[]{
                    firefighters.getX() * 16, firefighters.getY() * 16,
                    (firefighters.getX() * 16) + 28,(firefighters.getY() * 16),
                    (firefighters.getX() * 16) + 28, (firefighters.getY() * 16) + 28,
                    (firefighters.getX() * 16), (firefighters.getY() * 16) + 28
                });
        graphFireTruck.setAnimation(myFireTruck);
        graphFireTruck.setPolygon(myPolygon);
        
        return graphFireTruck;
    }
    
    public static GraphicablePoliceCar newPoliceCar(PoliceCar policeCar){
        GraphicablePoliceCar graphPoliceCar = new GraphicablePoliceCar(policeCar);
        
        Animation myPoliceCar = AnimationFactory.getPoliceCarAnimation();
        EmergencyEntityBuilding policeDepartment = CityEntities.buildings.get("Police Department");
        Polygon myPolygon = new Polygon(new float[]{
                    policeDepartment.getX() * 16, policeDepartment.getY() * 16,
                    (policeDepartment.getX() * 16) + 28,(policeDepartment.getY() * 16),
                    (policeDepartment.getX() * 16) + 28, (policeDepartment.getY() * 16) + 28,
                    (policeDepartment.getX() * 16), (policeDepartment.getY() * 16) + 28
                });
        graphPoliceCar.setAnimation(myPoliceCar);
        graphPoliceCar.setPolygon(myPolygon);
        
        return graphPoliceCar;
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
