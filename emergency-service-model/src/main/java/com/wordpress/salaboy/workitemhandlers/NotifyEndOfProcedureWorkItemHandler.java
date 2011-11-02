/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.workitemhandlers;

import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.messages.ProcedureCompletedMessage;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.hornetq.api.core.HornetQException;

/**
 * Used to notify the end of a single procedure
 * @author esteban
 */
public class NotifyEndOfProcedureWorkItemHandler implements WorkItemHandler{

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

        String emergencyId = ((Emergency) workItem.getParameter("emergency")).getId();
        String procedureId = (String) workItem.getParameter("concreteProcedureId");
        
        try {
            System.out.println("The procedure "+procedureId+" is finished!");
            MessageFactory.sendMessage(new ProcedureCompletedMessage(emergencyId, procedureId, new Date()));
        } catch (HornetQException ex) {
            Logger.getLogger(NotifyEndOfProcedureWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        manager.completeWorkItem(workItem.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        
    }
    
}
