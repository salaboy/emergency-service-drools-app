/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital;

/**
 *
 * @author salaboy
 */
public class Bed {
    private int id;

    public Bed(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Bed{" + "id=" + id + '}';
    }
    
    
}
