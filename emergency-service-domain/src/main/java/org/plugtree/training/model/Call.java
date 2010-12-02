package org.plugtree.training.model;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author salaboy
 */
public class Call implements Serializable{

    private Long id;
    private Date date;
    private Emergency emergency;
    private Long processId;
    public static AtomicLong incrementalId = new AtomicLong();

    public Call() {
        this.id = Call.incrementalId.getAndIncrement();
    }

    public Call(Date date) {
        this();
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

    @Override
    public String toString() {
        return "Call{" + "id=" + id + ", date=" + date + ", emergency=" + emergency + ", processId=" + processId + '}';
    }
    
}