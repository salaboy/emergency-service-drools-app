package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author salaboy
 */
public class Call implements Serializable {

    private String id;
    private Date date;
    private Long processId;
    private String phoneNumber;
    
    private int x; //location detected from the phonenumber :)
    private int y; //location detected from the phonenumber :)
    private String time;
    private transient Calendar calendar;
    private boolean processed = false;

    public Call() {
    
    }

    public Call(int x, int y, Date date) {
        calendar = Calendar.getInstance();
        this.date = date;
        this.phoneNumber = generateRandomePhoneNumber();
        this.x = x;
        this.y = y;
    }

    private String generateRandomePhoneNumber() {
        NumberFormat formatter = new DecimalFormat("0000");
        return "(555 -" + formatter.format(Math.random() * 1000) + ")";
    }

    public Date getDate() {
        return date;
    }

    public void setTime(String timeString) {
        this.time = timeString;
    }

    public String getTime() {
        time = "";
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(this.date.getTime());
        time += calendar.get(Calendar.MONTH) + "/"
                + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) + " - "
                + calendar.get(Calendar.HOUR) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND);

        return time;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public String toString() {
        return "Call{" + "id=" + id + ", date=" + date + ", processId=" + processId + ", phoneNumber=" + phoneNumber + ", x=" + x + ", y=" + y + ", time=" + time + ", calendar=" + calendar + ", processed=" + processed + '}';
    }
}