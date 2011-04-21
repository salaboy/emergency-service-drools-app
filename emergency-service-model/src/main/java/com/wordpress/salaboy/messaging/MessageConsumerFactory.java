/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

/**
 *
 * @author salaboy
 */
public class MessageConsumerFactory {
    public static MessageConsumer createMessageConsumer(String queueName){
        return new MessageConsumer(queueName);
    }
}
