/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.messaging.MessageConsumerWorker;
import com.wordpress.salaboy.messaging.MessageConsumerWorkerHandler;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.services.PhoneCallsMGMTService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author esteban
 */
public class CoreServer {

    public static void main(String[] args) throws Exception {
        new CoreServer().startServer();
    }

    public void startServer() throws Exception {
        MessageServerSingleton.getInstance().start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                try {
                    MessageServerSingleton.getInstance().stop();
                } catch (Exception ex) {
                    Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        startWorkers();
        
    }

    private void startWorkers() {
        try {
            
            //Phone Calls Worker
            MessageConsumerWorker phoneCallsWorker = new MessageConsumerWorker("phoneCalls", new MessageConsumerWorkerHandler<Call>() {
                @Override
                public void handleMessage(Call call) {
                    PhoneCallsMGMTService.getInstance().newPhoneCall(call);
                }
            }); 
            
            phoneCallsWorker.start();
            phoneCallsWorker.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
