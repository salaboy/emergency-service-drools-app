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
public interface EmergencyEntityBuilding extends Serializable {
    public enum EntityBuildingType{FIREFIGHTERS_DEPARTMENT, POLICE_DEPARMENT, BUILDING_911, HOSPITAL }
    public String getId();
    public void setId(String id);
    public int getX();
    public int getY();
    public String getName();
    public EntityBuildingType getType();
}
