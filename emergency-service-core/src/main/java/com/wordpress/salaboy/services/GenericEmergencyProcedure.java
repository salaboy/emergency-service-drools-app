/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.events.AllProceduresEndedEvent;

/**
 *
 * @author salaboy
 */
public interface GenericEmergencyProcedure{
    public void allProceduresEnededNotification(AllProceduresEndedEvent event);
    public void procedureCompletedNotification(String emergencyId, String procedureId);
    public void newPhoneCall(Call call);
    
}
