/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import java.util.Map;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class ProcedureServiceFactory {
    
    public static ProcedureService createProcedureService(String callId, String procedureName, Map<String, Object> parameters){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "procedures-beans.xml" );
        ProcedureService procedureService = (ProcedureService) context.getBean(procedureName);
        procedureService.configure(callId, parameters);
        return procedureService;
               
    }
    
    
    
    
    
    
}
