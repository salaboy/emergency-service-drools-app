/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.roles;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public interface Role extends Serializable{
    public String getId();
    public String getName();
    public String getStringId();
    public void setStringId(String stringId);
}
