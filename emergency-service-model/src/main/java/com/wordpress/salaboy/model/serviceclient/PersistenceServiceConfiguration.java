/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import java.util.Map;

/**
 *
 * @author salaboy
 */
public class PersistenceServiceConfiguration {
    private Map<String, Object> parameters;

    public PersistenceServiceConfiguration(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    
}
