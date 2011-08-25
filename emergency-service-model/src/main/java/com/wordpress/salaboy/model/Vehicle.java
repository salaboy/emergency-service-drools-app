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
public interface Vehicle extends Serializable {
    public String getId();
    public String getName();
    public float getPositionX();
    public float getPositionY();
    public void setPositionX(float x);
    public void setPositionY(float y);
}
