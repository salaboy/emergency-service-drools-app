/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.context.tracking;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.ImpermanentGraphDatabase;

/**
 *
 * @author salaboy
 */
public class ContextTrackingProvider {
    private static Map<ContextTrackingServiceType, ContextTrackingService> instances = new HashMap<ContextTrackingServiceType, ContextTrackingService>();
    public enum ContextTrackingServiceType {EMBEDDED, IN_MEMORY, REST};
    public static String defaultDB = "db/graph";
    
    public static ContextTrackingService getTrackingService(ContextTrackingServiceType type) throws IOException{
        if(instances.get(type) == null){
            switch(type){
                case IN_MEMORY: 
                        instances.put(type, new ContextTrackingServiceImpl(new ImpermanentGraphDatabase()));
                        break;
                case EMBEDDED: 
                        instances.put(type, new ContextTrackingServiceImpl(new EmbeddedGraphDatabase(defaultDB)) );
                        break; 
                case REST: 
                        throw new UnsupportedOperationException("Not Implemented YET!");
                         
                    
            }
            
        }
        return instances.get(type);
    }
    
   
}
