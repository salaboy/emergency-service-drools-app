package com.wordpress.salaboy.model.events;

import com.wordpress.salaboy.model.Call;

/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/11/11
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class IncomingCallEvent {

    private Call incomingCall;

    public IncomingCallEvent(Call incomingCall) {
        this.incomingCall = incomingCall;
    }

    public Call getIncomingCall() {
        return incomingCall;
    }
}
