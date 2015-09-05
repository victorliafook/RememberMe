package com.foftware.rememberme;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Victor on 15/07/2015.
 */
public class RememberTask {
    private long id;
    private String description;
    private Date date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String toJSON(){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("id", getId());
            jsonObject.put("date", getDate());
            jsonObject.put("time", getTime());
            jsonObject.put("description", getDescription());
            jsonObject.put("alarmOn", getAlarm());

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }
}
