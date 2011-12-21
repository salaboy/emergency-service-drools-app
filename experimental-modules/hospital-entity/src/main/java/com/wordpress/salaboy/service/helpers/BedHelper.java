/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.service.helpers;

import com.wordpress.salaboy.hospital.Bed;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author salaboy
 */
public class BedHelper {
    private static AtomicInteger bedAutoID = new AtomicInteger(1);
    public static List<Bed> initializeBeds(int number){
        List<Bed> beds = new ArrayList<Bed>();
        for(int i = 0; i < number; i++){
            beds.add(new Bed(bedAutoID.getAndIncrement()));
        }
        return beds;
    }
}
