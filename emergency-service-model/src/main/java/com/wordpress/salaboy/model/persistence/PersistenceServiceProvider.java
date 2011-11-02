/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.persistence;

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


    public static void clear() {
        configFile = "config-beans.xml";
        instance.clear();
        instance = null;
    }
}
