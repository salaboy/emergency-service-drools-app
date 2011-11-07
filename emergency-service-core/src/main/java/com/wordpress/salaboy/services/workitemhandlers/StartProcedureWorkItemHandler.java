/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services.workitemhandlers;

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.services.ProceduresMGMTServiceImpl;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 *
 * @author salaboy
 */
public class StartProcedureWorkItemHandler implements WorkItemHandler, Serializable{

    @Override
    public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
        //Call Tracking Component for process?? or the ProcedureMGMTService can take care of that?
        Call call = (Call) wi.getParameter("call");
        String procedureName = (String) wi.getParameter("procedureName");
        Emergency emergency = (Emergency) wi.getParameter("emergency");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);
        try {
            ProceduresMGMTServiceImpl.getInstance().newRequestedProcedure(emergency.getId(), procedureName, parameters);
        } catch (IOException ex) {
            Logger.getLogger(StartProcedureWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("I'm going out of here!!!!!!");
        wim.completeWorkItem(wi.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
