/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

/**
 *
 * @author salaboy
 */
public class MessageProducerFactory {
    public static MessageProducer createMessageProducer(String queueName){
        return new MessageProducer(queueName);
    }
}
