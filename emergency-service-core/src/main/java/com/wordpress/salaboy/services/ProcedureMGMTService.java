/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.EmergencyEvent;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public interface ProcedureMGMTService{

    void newRequestedProcedure(final String emergencyId, String procedureName, Map<String, Object> parameters) throws IOException;

    /**
     * Notifies all procedures of an emergency about a particular message.
     * The emergency is taken from {@link EmergencyInterchangeMessage#getCallId()}
     * Here is where message -> event -> service mapping is created
     * @param callId
     * @param event
     * @return
     */
    void notifyProcedures(EmergencyEvent event);
    
}
