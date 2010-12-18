/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.log;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;

/**
 *
 * @author esteban
 */
public class ProcessEventLogger implements ProcessEventListener {

    private Logger logger;

    public ProcessEventLogger(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void beforeProcessStarted(ProcessStartedEvent event) {
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        this.logger.addMessage("Process started");
    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent event) {
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        this.logger.addMessage("Process ended");
    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        this.logger.addMessage("Entering '"+event.getNodeInstance().getNodeName()+"' node");
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        this.logger.addMessage("Leaving '"+event.getNodeInstance().getNodeName()+"' node");
    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent event) {
    }

}
