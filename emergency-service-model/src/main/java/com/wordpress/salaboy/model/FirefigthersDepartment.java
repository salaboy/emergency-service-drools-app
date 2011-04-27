/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

/**
 *
 * @author salaboy
 */
public class FirefigthersDepartment implements EmergencyEntityBuilding {
    private String name;
    private int x;
    private int y;

    public FirefigthersDepartment(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
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
