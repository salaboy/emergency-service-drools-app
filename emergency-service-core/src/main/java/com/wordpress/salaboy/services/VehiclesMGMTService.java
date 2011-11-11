/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.vehicles.Ambulance;
import com.wordpress.salaboy.model.vehicles.FireTruck;
import com.wordpress.salaboy.model.vehicles.Vehicle;
import com.wordpress.salaboy.model.events.EmergencyVehicleEvent;
import com.wordpress.salaboy.model.persistence.PersistenceService;
import com.wordpress.salaboy.model.persistence.PersistenceServiceProvider;
import java.util.HashMap;
import java.util.Map;

/**
 * Facade for VehicleMonitorService instances. It contains a VehicleMonitorService
 * for each dispatched vehicle.
 * 
 * @author esteban
 */
public class VehiclesMGMTService {
 
    private static VehiclesMGMTService instance;
    
    private PersistenceService persistenceService;
    
    /**
     * A Map to store each service of a vehicle. The key is the vehicle id
     */
    private final Map<String,VehicleMonitorService> vehicleServices = new HashMap<String, VehicleMonitorService>();
    
    private VehiclesMGMTService(){
        persistenceService = PersistenceServiceProvider.getPersistenceService();
    }
    
    public synchronized static VehiclesMGMTService getInstance() {
        if (instance == null) {
            instance = new VehiclesMGMTService();
        }
        return instance;
    }
    
    public void newVehicleDispatched(final String emergencyId, final String vehicleId) {
        synchronized(vehicleServices){
            if (vehicleServices.containsKey(vehicleId)){
                throw new IllegalStateException("A monitor service is already configured for this vehicle: "+vehicleId);
            }

            //get the vehicle from the persistent store
            Vehicle vehicle = persistenceService.loadVehicle(vehicleId);
            if (vehicle == null){
                throw new IllegalArgumentException("Unknown Vehicle "+vehicleId);
            }

            //instanciates a new VehicleMonitorService according to the type of vehicle
            VehicleMonitorService service = null;
            if (vehicle instanceof Ambulance){
                service = new AmbulanceMonitorService();
            }else if (vehicle instanceof FireTruck){
                service = new FireTruckMonitorService(vehicleId);
            }else{
                throw new IllegalArgumentException("Don't know how to handle a "+vehicle);
            }

            //initialize the service
            service.newVehicleDispatched(emergencyId, vehicleId);

            //store the service 
            this.vehicleServices.put(vehicleId, service);
        }
    }
    
    public void processEvent(EmergencyVehicleEvent event){
        synchronized(vehicleServices){
            String vehicleId = event.getVehicleId();
            if (!vehicleServices.containsKey(vehicleId)){
                throw new IllegalStateException("No Monitor configured for this vehicle("+vehicleId+"). Did you forget to despatch it?");
            }
            VehicleMonitorService service = vehicleServices.get(event.getVehicleId());
            
            service.processEvent(event);
        }
    }
    
    public void vehicleRemoved(String vehicleId){
        synchronized(vehicleServices){
            if (!vehicleServices.containsKey(vehicleId)){
                throw new IllegalStateException("No Monitor configured for this vehicle("+vehicleId+"). Did you forget to despatch it?");
            }
            VehicleMonitorService service = vehicleServices.remove(vehicleId);
            service.vehicleRemoved();
        }
    }
    
}
