/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.service.helpers;

import java.util.Map;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 *
 * @author salaboy
 */
public class AutoHumanWorkItemHandler implements WorkItemHandler{
    private long workItemId;
    private Map<String, Object> input;
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        workItemId = workItem.getId();
        System.out.println(">>> Scheduling a Human Interaction -> "+workItem.getId());
        System.out.println(">>>>> For Process Instance -> "+workItem.getProcessInstanceId());
        System.out.println(">>>>> Variables: ");
        input = workItem.getParameters();
        for(String key : workItem.getParameters().keySet()){
            System.out.println(">>>>>>> Key: "+key + "  -- Value: "+workItem.getParameter(key));
        }
        
        
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public Map<String, Object> getInput() {
        return input;
    }
    
    
    
    
}
