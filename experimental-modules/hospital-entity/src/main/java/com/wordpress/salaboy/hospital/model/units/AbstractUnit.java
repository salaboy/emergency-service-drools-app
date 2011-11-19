/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital.model.units;

/**
 *
 * @author salaboy
 */
public class AbstractUnit implements Unit {

    protected String id;
    protected String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
