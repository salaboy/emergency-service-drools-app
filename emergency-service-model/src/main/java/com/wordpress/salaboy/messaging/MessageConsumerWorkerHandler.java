/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.messaging;

import java.lang.reflect.ParameterizedType;

/**
 *
 * @author esteban
 */
public abstract class MessageConsumerWorkerHandler<T>{
    
    private Class<T> type;

    public MessageConsumerWorkerHandler() {
        this.type = (Class<T>) ((ParameterizedType) getClass()
                                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    public void handlePrimitiveMessage(Object content){
        
        if (type.isAssignableFrom(content.getClass())){
            this.handleMessage((T)content);
        }
        
    }
    
    public abstract void handleMessage(T content);
    
}
