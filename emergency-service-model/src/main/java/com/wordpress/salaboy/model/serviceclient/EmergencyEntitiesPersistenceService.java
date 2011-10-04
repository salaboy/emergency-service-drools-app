/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FirefightersDepartment;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Patient;
import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.ServiceChannel;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.reporting.Report;
import java.util.Collection;

/**
 *
 * @author salaboy
 */
public interface EmergencyEntitiesPersistenceService {

    
    
    //Get all

    public Collection<Emergency> getAllEmergencies();

    public Collection<FirefightersDepartment> getAllFirefighterDepartments();

    public Collection<Hospital> getAllHospitals();

    public Collection<Vehicle> getAllVehicles();

    

    //Load by ID

    public ServiceChannel loadServiceChannel(String channelId);
    
    public Procedure loadProcedure(String procedureId);
    
    public Report loadReport(String callId);
    
    public Hospital loadHospital(String id);

    public Patient loadPatient(String id);

    public Vehicle loadVehicle(String id);
    
    public Call loadCall(String id);

    public Emergency loadEmergency(String id);
    
    public FirefightersDepartment loadFirefighterDepartment(String id);
   
    //Stores
    public void storeServiceChannel(ServiceChannel channel);
    
    public void storeProcedure(Procedure procedure);
    
    public void storeCall(Call call);
    
    public void storeEmergency(Emergency emergency);

    public void storeFirefightersDepartment(FirefightersDepartment firefightersDepartment);

    public void storeHospital(Hospital hospital);

    public void storePatient(Patient patient);

    public void storeVehicle(Vehicle vehicle);
    
    
    //Helpers
    public void addEntryToReport(String callId, String entry);
    
}
