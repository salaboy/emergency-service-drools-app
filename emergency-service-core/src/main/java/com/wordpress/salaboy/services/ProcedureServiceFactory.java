/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;
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
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
        persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);

        trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType) conf.getParameters().get("ContextTrackingImplementation"));
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "procedures-beans.xml" );
        ProcedureService procedureService = (ProcedureService) context.getBean(procedureName);
        String procedureId = trackingService.newProcedureId();
        procedureService.setId(procedureId);
//        ContextTrackingServiceImpl.getInstance().attachProcedure(emergencyId, procedureId);
        //TODO Uncomment this last line when it works neo4j between distinct JVM. 
        procedureService.configure(emergencyId, parameters);
        return procedureService;
               
    }
    
    
    
    
    
    
}
