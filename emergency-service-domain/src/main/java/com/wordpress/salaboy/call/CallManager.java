/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.call;

import com.wordpress.salaboy.call.IncomingCallListener;
import java.util.ArrayList;
import java.util.List;
import com.wordpress.salaboy.model.Call;

/**
 *
 * @author esteban
 */
public class CallManager {
    
    private static CallManager INSTANCE;
    
    private List<IncomingCallListener> listeners = new ArrayList<IncomingCallListener>();
    
    private CallManager(){
        
    }
    
    public synchronized static CallManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CallManager();
        }
        return INSTANCE;
    }
    
    public void incomingCall(Call call){
        //notify listeners:
        for (IncomingCallListener listener : listeners) {
            listener.processIncomingCall(call);
        }
    }
    
    public void addIncomingCallListener(IncomingCallListener listener){
        listeners.add(listener);
    }
}
