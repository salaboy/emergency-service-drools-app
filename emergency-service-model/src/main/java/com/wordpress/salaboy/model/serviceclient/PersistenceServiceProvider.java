/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class PersistenceServiceProvider {

    private static ApplicationContext context;
    private static PersistenceService instance;
    public static String configFile = "config-beans.xml";
    public enum PersistenceServiceType {

        DISTRIBUTED_MAP, JPA, SERVICE
    };
    
    public static PersistenceService getPersistenceService() {
        if (instance == null) {
            context = new ClassPathXmlApplicationContext(configFile);
            EnvironmentConfiguration conf = (EnvironmentConfiguration) context.getBean("environmentConf");
            PersistenceServiceType type = (PersistenceServiceType) conf.get("PersistenceService");
            if(type == null){
                throw new IllegalStateException("Persistence Service Type needs to be specified in spring");
            }
            switch (type) {
                case DISTRIBUTED_MAP:
                    instance =  new DistributedMapPeristenceService();
                    break;
                case JPA:
                    throw new UnsupportedOperationException("Not Implemented YET!");
                case SERVICE:
                    throw new UnsupportedOperationException("Not Implemented YET!");
                default:
                    instance =  new DistributedMapPeristenceService();

            }
            
        
        }

        return instance;
    
    }
//
//    public synchronized static PersistenceService getPersistenceService(PersistenceServiceType type) throws IOException {
//        return getPersistenceService(type, null);
//    }

//    public synchronized static PersistenceService getPersistenceService(PersistenceServiceType type, PersistenceServiceConfiguration conf) throws IOException {
//        if (instances == null) {
//            instances = new HashMap<PersistenceServiceType, PersistenceService>();
//        }
//        if (instances.get(type) == null) {
//            switch (type) {
//                case DISTRIBUTED_MAP:
//                    instances.put(type, new DistributedMapPeristenceService(conf));
//                    break;
//
//                case JPA:
//                    throw new UnsupportedOperationException("Not Implemented YET!");
//
//                case SERVICE:
//                    throw new UnsupportedOperationException("Not Implemented YET!");
//
//
//            }
//
//        }
//        return instances.get(type);
//    }

    public static void clear() {
//        for (PersistenceService service : instances.values()) {
//            service.clear();
//        }
//        instances.clear();
//        instances = null;
        instance.clear();
        instance = null;
    }
}
