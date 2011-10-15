/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.context.tracking;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.ImpermanentGraphDatabase;

/**
 *
 * @author salaboy
 */
public class ContextTrackingProvider {
    private static Map<ContextTrackingServiceType, ContextTrackingService> instances = new HashMap<ContextTrackingServiceType, ContextTrackingService>();
    public enum ContextTrackingServiceType {EMBEDDED, IN_MEMORY, REST};
    public static String defaultDB = "target/db/graph";
    
    public static ContextTrackingService getTrackingService(ContextTrackingServiceType type) throws IOException{
        if(instances == null){
            instances = new HashMap<ContextTrackingServiceType, ContextTrackingService>(); 
        }
        if(instances.get(type) == null){
            switch(type){
                case IN_MEMORY: 
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>> Creating a new Instance of ImpermanentGraphDatabase");
                        instances.put(type, new ContextTrackingServiceImpl(new ImpermanentGraphDatabase(defaultDB)));
                        break;
                case EMBEDDED: 
                        instances.put(type, new ContextTrackingServiceImpl(new EmbeddedGraphDatabase(defaultDB)) );
                        break; 
                case REST: 
                        throw new UnsupportedOperationException("Not Implemented YET!");
                default:
                      instances.put(type, new ContextTrackingServiceImpl(new ImpermanentGraphDatabase(defaultDB)));
                      break;
                    
            }
            
        }
        return instances.get(type);
    }
    
    
    public static void clear(){
        for(ContextTrackingService service : instances.values()){
            service.clear();
        }
        instances.clear();
        instances = null;
    }
     
}
