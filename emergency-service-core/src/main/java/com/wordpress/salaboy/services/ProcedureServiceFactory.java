/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class ProcedureServiceFactory {
    
    public static ProcedureService createProcedureService(Long callId, String procedureName){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext( "procedure-beans.xml" );
        ProcedureService procedureService = (ProcedureService) context.getBean(procedureName);
        procedureService.configure(callId);
        return procedureService;
               
    }
    
    
    
    
    
    
}
