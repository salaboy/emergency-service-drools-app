/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

/**
 *
 * @author salaboy
 */
public class FirefightersDepartment implements EmergencyEntityBuilding {
    private Long id;
    private String name;
    private int x;
    private int y;

    public FirefightersDepartment(Long id,String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public String getName() {
        return name;
    }
    
    
}
