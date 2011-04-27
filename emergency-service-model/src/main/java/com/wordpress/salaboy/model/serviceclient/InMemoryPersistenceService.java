/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Patient;
import com.wordpress.salaboy.model.Vehicle;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author salaboy
 */
public class InMemoryPersistenceService implements Serializable {
    private Map<Long, Call> calls;
    private Map<Long, Emergency> emergencies;
    private Map<Long, Vehicle> vehicles;
    private Map<Long, Patient> patients;
    private static InMemoryPersistenceService instance;
    
    public static InMemoryPersistenceService getInstance(){
        if(instance == null){
            instance = new InMemoryPersistenceService();
        }
        return instance;
    }
    
    private InMemoryPersistenceService() {
        this.calls = new ConcurrentHashMap<Long, Call>();
        this.emergencies = new ConcurrentHashMap<Long, Emergency>();
        this.vehicles = new ConcurrentHashMap<Long, Vehicle>();
        this.patients = new ConcurrentHashMap<Long, Patient>();
    }
    
    public void storeCall(Call call){
        this.calls.put(call.getId(), call);
    }
    
    public void storeEmergency(Emergency emergency){
        this.emergencies.put(emergency.getId(), emergency);
    }
    
    public void storeVehicle(Vehicle vehicle){
        this.vehicles.put(vehicle.getId(), vehicle);
                
    }
    public void storePatient(Patient patient){
        this.patients.put(patient.getId(), patient);
    }
    
    public Call loadCall(Long id){
        return this.calls.get(id);
    }
    
    public Emergency loadEmergency(Long id){
        return this.emergencies.get(id);
    }
    
    public Vehicle loadVehicle(Long id){
        return this.vehicles.get(id);
    }
    
    public Patient loadPatient(Long id){
        return this.patients.get(id);
    }
    
    
    
    public void clear(){
        this.vehicles.clear();
        this.emergencies.clear();
        this.vehicles.clear();
    }
    
}
