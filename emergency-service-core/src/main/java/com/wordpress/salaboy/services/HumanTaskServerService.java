/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.wordpress.salaboy.MockUserInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.mina.MinaTaskServer;

/**
 *
 * @author salaboy
 */
public class HumanTaskServerService {
    private static HumanTaskServerService instance = null;
    private MinaTaskServer server;
    private EntityManagerFactory emf;
    private TaskService taskService;
    private TaskServiceSession taskSession;
    private PoolingDataSource ds1;
    private Map<String, TaskClient> currentClients;
    private HumanTaskServerService() {
    }
    
    public static HumanTaskServerService getInstance(){
        if(instance == null){
            instance = new HumanTaskServerService();
        }
        return instance;
    }
    public void initTaskServer() throws IOException {
        if(server != null && server.isRunning()){
            System.out.println(">>> Server Already Started");
            return;
        }
            
        
        System.out.println(">>> Starting Human Task Server ...");
       
        ds1 = new PoolingDataSource();
        ds1.setUniqueName("jdbc/testDS1");

        //ds1.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        ds1.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds1.setMaxPoolSize(5);
        ds1.setAllowLocalTransactions(true);
        ds1.getDriverProperties().put("user", "root");
        ds1.getDriverProperties().put("password", "atcroot");
        //ds1.getDriverProperties().put("databaseName", "droolsflow");
        //ds1.getDriverProperties().put("serverName", "localhost");

        ds1.init();
        // Use persistence.xml configuration
        emf = Persistence.createEntityManagerFactory("org.jbpm.task");

        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        taskSession = taskService.createSession();
        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserinfo(userInfo);

        User operator = new User("operator");
        User driver = new User("control");
        User hospital = new User("hospital");
        User doctor = new User("doctor");
        User garageEmergencyService = new User("garage_emergency_service");
        User Administrator = new User("Administrator");
        
        
        taskSession.addUser(Administrator);
        taskSession.addUser(operator);
        taskSession.addUser(driver);
        taskSession.addUser(hospital);
        taskSession.addUser(doctor);
        taskSession.addUser(garageEmergencyService);

        server = new MinaTaskServer(taskService);
        server.start();
        
        
        System.out.println(">>> Human Task Server Started!");
    }
    
    public void stopTaskServer() throws Exception{
       
        for(String key : currentClients.keySet()){
            System.out.println(">>> Disconnecting Client = "+key);
            currentClients.get(key).disconnect();
        }
        if(server != null && !server.isRunning()){
            System.out.println(">>> Server Already Stopped!");
            return;
        }
        
        System.out.println(">>> Stopping Human Task Server ...");
        System.out.println("????????????? -> "+server.getIoAcceptor().isCloseOnDeactivation());
        
        
        server.getIoAcceptor().unbind();
        
        ds1.close();
        taskSession.dispose();
        
        server.stop();
        
        server = null;
        taskSession = null;
        ds1 = null;
        
        System.out.println(">>> Human Task Server Stopped!");
    }
    
    public  TaskClient initTaskClient(String clientName) {
        TaskClient client = new TaskClient(new MinaTaskClientConnector(clientName,
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        boolean connected = client.connect("127.0.0.1", 9123);

        int retry = 0;
        while (!connected) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(HumanTaskServerService.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = client.connect("127.0.0.1", 9123);
            if (!connected) {
                retry++;
            }
        }
        System.out.println("Client ("+clientName+") Connected after " + retry + " retries");
        if(currentClients == null){
            currentClients = new HashMap<String, TaskClient>();
        }
        currentClients.put(clientName, client);
        return client;
    }
}
