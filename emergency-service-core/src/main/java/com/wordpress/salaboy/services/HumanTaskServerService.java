/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.drools.SystemEventListenerFactory;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;
import org.jbpm.task.service.hornetq.HornetQTaskServer;

/**
 *
 * @author salaboy
 */
public class HumanTaskServerService {

    private static HumanTaskServerService instance = null;
    //private MinaTaskServer server;
    private HornetQTaskServer server;
    private EntityManagerFactory emf;
    private TaskService taskService;
    private TaskServiceSession taskSession;
    private PoolingDataSource ds1;
    private Map<String, TaskClient> currentClients;

    private HumanTaskServerService() {
    }

    public static HumanTaskServerService getInstance() {
        if (instance == null) {
            instance = new HumanTaskServerService();
        }
        return instance;
    }

    public void initTaskServer() {



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

        User operator = new User("operator");
        User driver = new User("control");
        User hospital = new User("hospital");
        User doctor = new User("doctor");
        User firefighter = new User("firefighter");
        User garageEmergencyService = new User("garage_emergency_service");
        User Administrator = new User("Administrator");


        taskSession.addUser(Administrator);
        taskSession.addUser(operator);
        taskSession.addUser(driver);
        taskSession.addUser(hospital);
        taskSession.addUser(doctor);
        taskSession.addUser(firefighter);
        taskSession.addUser(garageEmergencyService);

        if (server != null && server.isRunning()) {
            System.out.println(">>> Server Already Started");
            return;
        }
        //server = new MinaTaskServer(taskService);
        server = new HornetQTaskServer(taskService, 5446);

        try {
       
            Thread thread = new Thread( server );
            thread.start();

            
            
            while (!server.isRunning()) {
                System.out.print(".");
                Thread.sleep(50);
            }
        } catch (Exception ex) {
            Logger.getLogger(HumanTaskServerService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(" >>> ERROR: Server Not Started:  " + ex.getMessage());
        }



        if (server.isRunning()) {
            System.out.println(">>> Human Task Server Started!");
        }
    }

    public void stopTaskServer() {
        if (currentClients != null) {
            for (String key : currentClients.keySet()) {
                System.out.println(">>> Disconnecting Client = " + key);
                try {
                    currentClients.get(key).disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(HumanTaskServerService.class.getName()).log(Level.SEVERE, null, ex);
                }
                currentClients.remove(key);
            }
            currentClients = null;
        }


        System.out.println(">>> Stopping Human Task Server ...");



        ds1.close();
        taskSession.dispose();
        try {
            server.stop();
        } catch (Exception ex) {
            Logger.getLogger(HumanTaskServerService.class.getName()).log(Level.SEVERE, null, ex);
        }



        server = null;
        taskSession = null;
        ds1 = null;

        System.out.println(">>> Human Task Server Stopped!");
    }

    public TaskClient initTaskClient() {
        TaskClient client = new TaskClient(new HornetQTaskClientConnector("tasksQueue/appclient",
                new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        
        
        //boolean connected = client.connect("127.0.0.1", 9123);
        boolean connected = client.connect("127.0.0.1", 5446);

        int retry = 0;
        while (!connected) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(HumanTaskServerService.class.getName()).log(Level.SEVERE, null, ex);
            }
            //connected = client.connect("127.0.0.1", 9123);
            connected = client.connect("127.0.0.1", 5446);
            if (!connected) {
                retry++;
            }
        }


        System.out.println("Client (" + "tasksQueue" + ") Connected after " + retry + " retries");
        if (currentClients == null) {
            currentClients = new ConcurrentHashMap<String, TaskClient>();
        }
        currentClients.put("tasksQueue", client);


        return client;
    }
}
