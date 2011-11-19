/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.roles;

import java.util.UUID;

/**
 *
 * @author salaboy
 */
public class Nurse extends AbstractRole{

    public Nurse(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    
    
}
