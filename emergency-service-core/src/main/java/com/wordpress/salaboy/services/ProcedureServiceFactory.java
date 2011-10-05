/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;
import java.util.Map;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class ProcedureServiceFactory {
    
    public static ProcedureService createProcedureService(String emergencyId, String procedureName, Map<String, Object> parameters){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "procedures-beans.xml" );
        ProcedureService procedureService = (ProcedureService) context.getBean(procedureName);
        String procedureId = ContextTrackingServiceImpl.getInstance().newProcedureId();
        procedureService.setId(procedureId);
//        ContextTrackingServiceImpl.getInstance().attachProcedure(emergencyId, procedureId);
        //TODO Uncomment this last line when it works neo4j between distinct JVM. 
        procedureService.configure(emergencyId, parameters);
        return procedureService;
               
    }
    
    
    
    
    
    
}
