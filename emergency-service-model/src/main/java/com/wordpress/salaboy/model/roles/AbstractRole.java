/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.roles;

/**
 *
 * @author salaboy
 */
public class AbstractRole implements Role {
    protected String id;
    protected String name;
    protected String stringId;
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getStringId() {
        return this.stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }
    
    
}
