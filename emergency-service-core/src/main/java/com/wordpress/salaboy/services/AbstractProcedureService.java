/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import java.util.Map;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author salaboy
 */
public abstract class AbstractProcedureService implements ProcedureService {

    protected StatefulKnowledgeSession internalSession;
    protected String procedureName;
    protected boolean useLocalKSession = true;

    public AbstractProcedureService(String procedureName) {
        this.procedureName = procedureName;
    }
   
    @Override
    public abstract void configure(String emergencyId, Procedure procedure, Map<String, Object> parameters);

    @Override
    public abstract void procedureEndsNotification(EmergencyEndsEvent event);
    
    public String getProcedureName() {
        return procedureName;
    }

    public boolean isUseLocalKSession() {
        return useLocalKSession;
    }

    public void setUseLocalKSession(boolean useLocalKSession) {
        this.useLocalKSession = useLocalKSession;
    }
}
