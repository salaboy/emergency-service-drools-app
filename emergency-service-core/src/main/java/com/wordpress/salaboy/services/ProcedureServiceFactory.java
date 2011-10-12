/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class ProcedureServiceFactory {
    private static PersistenceService persistenceService;
    private static ContextTrackingService trackingService;
    
    public static ProcedureService createProcedureService(String emergencyId, String procedureName, Map<String, Object> parameters) throws IOException{
        //initialize tracking and persistence services
        initializePersistenceAndTrackingServices();
        
        //create a new Procedure: this is the representation of the Procedure Service
        Procedure newProcedure = new Procedure(procedureName);
        
        //Get the requested ProcedureService from Spring
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "procedures-beans.xml" );
        ProcedureService procedureService = (ProcedureService) context.getBean(procedureName);
        
        //Store the procedure so it has a valid id
        persistenceService.storeProcedure(newProcedure);
        
        //Configure the ProcedureService
        procedureService.configure(emergencyId, newProcedure, parameters);
        
        //Update the procedure.
        persistenceService.storeProcedure(newProcedure);
        
        //the process is attached to the emergency
        trackingService.attachProcedure(emergencyId, newProcedure.getId());
        
        return procedureService;
    }
    
    private synchronized static void initializePersistenceAndTrackingServices() throws IOException{
        if (persistenceService == null){
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
            PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
            persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);
            trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType) conf.getParameters().get("ContextTrackingImplementation"));
        }
    }
    
    
    
    
    
    
}
