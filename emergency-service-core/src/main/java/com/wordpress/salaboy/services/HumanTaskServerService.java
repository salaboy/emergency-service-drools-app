/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

/**
 *
 * @author salaboy
 */
public class HumanTaskServerService {
    private static final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();
    private static HumanTaskServerService instance = null;

    private HumanTaskServerService() {
    }
    
    public static HumanTaskServerService getInstance(){
        if(instance == null){
            instance = new HumanTaskServerService();
        }
        return instance;
    }
    public void initTaskServer() {


        Runtime.getRuntime().addShutdownHook(new Thread()  {

            public void run() {
                System.out.println("\n");
                taskServerDaemon.stopServer();
                System.out.println("server stoped...");
            }
        });

        taskServerDaemon.startServer();
        System.out.println("server started... (ctrl-c to stop it)");
    }
    
    public void stopTaskServer(){
        System.out.println("server stoping...");
        taskServerDaemon.stopServer();
    }
}
