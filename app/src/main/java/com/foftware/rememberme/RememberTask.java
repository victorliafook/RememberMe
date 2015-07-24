package com.foftware.rememberme;

import java.util.Date;

/**
 * Created by Victor on 15/07/2015.
 */
public class RememberTask {
    private long id;
    private String description;
    private Date time;
    private Boolean alarmOn;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Boolean getAlarm(){
        return alarmOn;
    }

    public void setAlarm(Boolean alarmOn){
        this.alarmOn = alarmOn;
    }
}
