/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.call;

import org.plugtree.training.model.Call;

/**
 *
 * @author esteban
 */
public interface IncomingCallListener {
    void processIncomingCall(Call call);
}
