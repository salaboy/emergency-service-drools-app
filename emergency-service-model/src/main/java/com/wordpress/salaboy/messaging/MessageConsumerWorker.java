/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.messaging;

import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.messaging.MessageConsumerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;

/**
 *
 * @author esteban
 */
public class MessageConsumerWorker extends Thread{
    
    private final MessageConsumer consumer;
    private final MessageConsumerWorkerHandler handler;
    private boolean stopExecution;

    public MessageConsumerWorker(String queueName, MessageConsumerWorkerHandler handler) {
        consumer = MessageConsumerFactory.createMessageConsumer(queueName);
        this.handler = handler;
    }
    
    public void stopWorker(){
        try {
            this.stopExecution = true;
            consumer.stop();
        } catch (HornetQException ex) {
            Logger.getLogger(MessageConsumerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (!stopExecution){
            try {
                Object receivedMessage = consumer.receiveMessage();
                this.handler.handleMessage(receivedMessage);
            } catch (HornetQException ex) {
                Logger.getLogger(MessageConsumerWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    
}
