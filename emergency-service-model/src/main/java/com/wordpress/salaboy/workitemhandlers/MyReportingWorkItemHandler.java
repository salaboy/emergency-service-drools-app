/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.workitemhandlers;

import java.io.Serializable;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 *
 * @author salaboy
 */
public class MyReportingWorkItemHandler implements WorkItemHandler, Serializable{

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.out.println(">>>>>>>>REPORTING!!!!!");
         String priority = (String)workItem.getParameter("emergency.priority");
         System.out.println("Priority = "+priority);
         manager.completeWorkItem(workItem.getId(), null);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        
    }


}
