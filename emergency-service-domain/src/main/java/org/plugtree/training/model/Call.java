package org.plugtree.training.model;

import java.util.Date;

/**
 *
 * @author salaboy
 */
public class Call {

    private Long id;
    private Date date;
    private Emergency emergency;
    private Long processId;

    public Call() {
    }

    public Call(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Emergency getEmergency() {
        return emergency;
    }

    public void setEmergency(Emergency emergency) {
        this.emergency = emergency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }
}