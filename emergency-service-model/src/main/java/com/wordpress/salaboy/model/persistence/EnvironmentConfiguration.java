/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.persistence;

import java.util.Map;

/**
 *
 * @author salaboy
 */
public class EnvironmentConfiguration {

    private Map<String, Object> config;

    public EnvironmentConfiguration() {
    }

    public EnvironmentConfiguration(Map<String, Object> config) {
        this.config = config;
    }

    public Object put(String k, Object v) {
        return config.put(k, v);
    }

    public Object get(Object o) {
        return config.get(o);
    }
    
    
}
