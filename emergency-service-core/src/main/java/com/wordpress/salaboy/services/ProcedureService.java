/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public interface ProcedureService {

    
    public void procedureEndsNotification(EmergencyEndsEvent event);
    
    /**
     * Configuration method for the Procedure. 
     * @param emergencyId
     * @param procedure The procedure to configure. Implementations of 
     * this method MUST set a value for procedure.processInstanceId
     * @param parameters
     */
    public void configure(String emergencyId, Procedure procedure, Map<String, Object> parameters);
    
    public String getProcedureId();
    
    public String getEmergencyId();

}
