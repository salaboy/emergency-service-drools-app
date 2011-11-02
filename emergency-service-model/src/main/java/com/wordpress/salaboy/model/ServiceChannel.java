/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class ServiceChannel implements Serializable {
    private String id;
    private String name;

    public ServiceChannel() {
    }

    public ServiceChannel(String name) {
        this.name = name;
    }
    
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ServiceChannel{" + "id=" + id + ", name=" + name + '}';
    }

    
   
    
}
