/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

/**
 *
 * @author esteban
 */
public abstract class MessageConsumerWorkerHandler<T>{
    
    public void handlePrimitiveMessage(Object content){
        this.handleMessage((T)content);
    }
    
    public abstract void handleMessage(T content);
    
}
