/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.Call;
import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class IncomingCallMessage implements Serializable, EmergencyInterchangeMessage {
    private Call call;

    public IncomingCallMessage(Call call) {
        this.call = call;
    }
    
    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    @Override
    public String getCallId() {
        return this.call.getId();
    }

    @Override
    public String toString() {
        return "IncomingCallMessage{" + "call=" + call + '}';
    }
    
    
    
}
