/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.context.tracking;

import com.wordpress.salaboy.model.serviceclient.EnvironmentConfiguration;

import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author salaboy
 */
public class ContextTrackingProvider {
    //  private static Map<ContextTrackingServiceType, ContextTrackingService> instances = new HashMap<ContextTrackingServiceType, ContextTrackingService>();

    private static ContextTrackingService instance;
    private static ApplicationContext context;

    public enum ContextTrackingServiceType {

        EMBEDDED, IN_MEMORY, REST
    };
    public static String defaultDB = "target/db/graph";
    public static final String SERVER_BASE_URL = "http://localhost:7575";
    public static String configFile = "config-beans.xml";
    public static ContextTrackingService getTrackingService() {
        if (instance == null) {
            context = new ClassPathXmlApplicationContext(configFile);
            EnvironmentConfiguration conf = (EnvironmentConfiguration) context.getBean("environmentConf");
            ContextTrackingServiceType type = (ContextTrackingServiceType) conf.get("ContextTrackingService");
            if (type == null) {
                throw new IllegalStateException("Persistence Service Type needs to be specified in spring");
            }
            switch (type) {
                case IN_MEMORY:
                    instance = new ContextTrackingServiceImpl(new ImpermanentGraphDatabase(defaultDB));
                    break;
                case EMBEDDED:
                    instance = new ContextTrackingServiceImpl(new EmbeddedGraphDatabase(defaultDB));
                    break;
                case REST:
                    instance = new ContextTrackingServiceRest(SERVER_BASE_URL);
                    break;
                default:
                    instance = new ContextTrackingServiceImpl(new ImpermanentGraphDatabase(defaultDB));
                    break;

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
