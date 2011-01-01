/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.call;

import com.wordpress.salaboy.model.Call;

/**
 *
 * @author esteban
 */
public interface IncomingCallListener {
    void processIncomingCall(Call call);
}
