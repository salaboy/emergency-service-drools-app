/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.netty.NettyAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;

/**
 *
 * @author salaboy
 */
public class MessageServerSingleton {
    private static MessageServerSingleton instance;
    private HornetQServer server;
    private MessageServerSingleton() throws Exception {
        //Server Configuration
        Configuration configuration = new ConfigurationImpl();
        configuration.setPersistenceEnabled(false);
        configuration.setSecurityEnabled(false);

        Map<String, Object> connectionParams = new HashMap<String, Object>();
        connectionParams.put(TransportConstants.PORT_PROP_NAME, 8050);

        TransportConfiguration transpConf = new TransportConfiguration(NettyAcceptorFactory.class.getName(), connectionParams);

        HashSet<TransportConfiguration> setTransp = new HashSet<TransportConfiguration>();
        setTransp.add(transpConf);

        configuration.setAcceptorConfigurations(setTransp);

        server = HornetQServers.newHornetQServer(configuration);

        
    }
    
    public static MessageServerSingleton getInstance() throws Exception{
        if(instance == null){
            instance = new MessageServerSingleton();
        }
        return instance;
    }
    
    public void start() throws Exception{
       server.start(); 
    }
    
    public void stop() throws Exception{
       server.stop();
    }
    
}
