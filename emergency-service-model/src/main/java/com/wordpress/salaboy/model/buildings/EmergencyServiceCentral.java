/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.buildings;

/**
 *
 * @author salaboy
 */
public class EmergencyServiceCentral implements EntityBuilding {

    private String id;
    private int x;
    private int y;
    private String name;

    public EmergencyServiceCentral(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
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

    @Override
    public EntityBuildingType getType() {
        return EntityBuildingType.BUILDING_911;
    }

    @Override
    public String toString() {
        return "EmergencyServiceCentral{" + "id=" + id + ", x=" + x + ", y=" + y + ", name=" + name + '}';
    }
}
