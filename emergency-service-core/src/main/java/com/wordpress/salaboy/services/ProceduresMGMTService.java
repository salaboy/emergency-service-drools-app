/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import java.util.HashMap;
import java.util.Map;



/**
 *
 * @author salaboy
 */
public class ProceduresMGMTService {

    private static ProceduresMGMTService instance;
    private Map<Long, ProcedureService> procedureService;

    private ProceduresMGMTService() {
        procedureService = new HashMap<Long, ProcedureService>();

    }

    public static ProceduresMGMTService getInstance() {
        if (instance == null) {
            instance = new ProceduresMGMTService();
        }
        return instance;
    }

    public void newRequestedProcedure(final Long callId, String procedureName, Map<String, Object> parameters) {

        procedureService.put(callId, ProcedureServiceFactory.createProcedureService(callId, procedureName));

    }
}
