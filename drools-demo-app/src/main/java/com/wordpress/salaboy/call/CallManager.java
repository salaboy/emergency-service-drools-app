/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.plugtree.training.model.Call;

/**
 *
 * @author esteban
 */
public class CallManager {
    
    private static CallManager INSTANCE;
    
    private Map<Long, Call> calls = new HashMap<Long, Call>();
    private List<IncomingCallListener> listeners = new ArrayList<IncomingCallListener>();
    
    private CallManager(){
        
    }
    
    public synchronized static CallManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CallManager();
        }
        return INSTANCE;
    }
    
    public void incomingCall(Long id, Call call){
        calls.put(id, call);
        
        //notify listeners:
        for (IncomingCallListener listener : listeners) {
            listener.processIncomingCall(id, call);
        }
    }
    
    public void addIncomingCallListener(IncomingCallListener listener){
        listeners.add(listener);
    }

    public Map<Long, Call> getCalls() {
        return Collections.unmodifiableMap(calls);
    }
    
}
