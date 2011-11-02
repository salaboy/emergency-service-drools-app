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
public class Procedure implements Serializable{
    private String id;
    private String name;
    private Long processInstanceId;

    public Procedure(String name) {
        this.name = name;
    }

    public Procedure() {
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

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String toString() {
        return "Procedure{" + "id=" + id + ", name=" + name + ", processInstanceId=" + processInstanceId + '}';
    }
    
}
