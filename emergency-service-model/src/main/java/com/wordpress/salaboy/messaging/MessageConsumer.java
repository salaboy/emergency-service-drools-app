/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;

/**
 *
 * @author salaboy
 */
public class MessageConsumer {
    private ClientSession consumerSession;
    private ClientConsumer consumer;
    private String queueName;

    public MessageConsumer(String queueName, ClientSessionFactory factory) {
        this.queueName = queueName;
        try {
            consumerSession = factory.createSession(true,true);
            consumer = consumerSession.createConsumer(queueName);
            consumerSession.start();
        } catch (HornetQException ex) {
            Logger.getLogger(MessageConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getQueueName() {
        return queueName;
    }
    
    public Object receiveMessage() throws HornetQException {
        ObjectInputStream ois = null;
        try {
            ClientMessage msgReceived = consumer.receive();
            int length = msgReceived.getBodyBuffer().readInt();
            byte[] data = new byte[length];
            msgReceived.getBodyBuffer().readBytes(data);
            ois = new org.hornetq.utils.ObjectInputStreamWithClassLoader(new ByteArrayInputStream(data));
            return ois.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MessageConsumer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException("Unable to read Message body: "+ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(MessageConsumer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException("Unable to read Message body: "+ex.getMessage());
        } finally {
            try {
                if (ois != null){
                    ois.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(MessageConsumer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    public void stop() throws HornetQException {
        consumer.close();
        consumerSession.stop();
        consumerSession.close();
    }
    
    

}
