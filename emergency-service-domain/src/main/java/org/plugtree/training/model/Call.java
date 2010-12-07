package org.plugtree.training.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    private String phoneNumber;
    public static AtomicLong incrementalId = new AtomicLong();

    public Call() {
        this.id = Call.incrementalId.getAndIncrement();
    }

    public Call(Date date) {
        this();
        this.date = date;
        NumberFormat formatter = new DecimalFormat("0000");
        this.phoneNumber = "(555 -" + formatter.format(Math.random()*1000) + ")";
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
    @Override
    public String toString() {
        return "Call{" + "id=" + id + ", date=" + date + ", emergency=" + emergency + ", processId=" + processId + '}';
    }
    
}