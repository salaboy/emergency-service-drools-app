/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author salaboy
 */
public class PersistenceServiceProvider {

    private static Map<PersistenceServiceType, PersistenceService> instances =  new HashMap<PersistenceServiceType, PersistenceService>();

    public enum PersistenceServiceType {

        DISTRIBUTED_MAP, JPA, SERVICE
    };

     public synchronized static PersistenceService getPersistenceService(PersistenceServiceType type) throws IOException {
        return getPersistenceService(type, null);
    }
    
    public synchronized static PersistenceService getPersistenceService(PersistenceServiceType type, PersistenceServiceConfiguration conf) throws IOException {
        if (instances.get(type) == null) {
            switch (type) {
                case DISTRIBUTED_MAP:
                    instances.put(type, new DistributedMapPeristenceService(conf));
                    break;

                case JPA:
                    throw new UnsupportedOperationException("Not Implemented YET!");

                case SERVICE:
                    throw new UnsupportedOperationException("Not Implemented YET!");


            }

        }
        return instances.get(type);
    }
    
    
}
