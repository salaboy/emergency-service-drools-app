/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital.model.units;

import com.wordpress.salaboy.hospital.Bed;
import java.util.List;

/**
 *
 * @author salaboy
 */
public abstract class AbstractUnit implements Unit {

    protected String id;
    protected String name;
    protected List<Bed> beds;
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    
    
}
