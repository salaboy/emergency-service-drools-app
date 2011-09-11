/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.workitemhandlers;

import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.messages.VehicleDispatchedMessage;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.hornetq.api.core.HornetQException;

/**
 *
 * @author esteban
 */
public class DispatchSelectedVehiclesWorkItemHandler implements WorkItemHandler{

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String callId = ((Call) workItem.getParameter("call")).getId();
        Vehicle vehicle = (Vehicle) workItem.getParameter("vehicle");
        try {
            MessageFactory.sendMessage(new VehicleDispatchedMessage(callId, vehicle.getId()));
        } catch (HornetQException ex) {
            Logger.getLogger(DispatchSelectedVehiclesWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        manager.completeWorkItem(workItem.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        
    }
    
}
