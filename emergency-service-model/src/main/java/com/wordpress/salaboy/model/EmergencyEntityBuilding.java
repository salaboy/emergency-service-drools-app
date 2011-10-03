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
    public int getX();
    public int getY();
    public String getName();
}
