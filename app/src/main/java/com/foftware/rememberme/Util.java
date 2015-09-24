package com.foftware.rememberme;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Victor on 29/07/2015.
 */
public class Util {
    public final static String debugTag = "REMEMBER_ME_APP";

    public static String padInt(int inumber){
        String strInt;
        if(inumber < 10){
            strInt = "0" + inumber;
        }else{
            strInt = String.valueOf(inumber);
        }
        return strInt;
    }

    public static long dateDifference(Date from, Date to){
        long diff = to.getTime() - from.getTime();
        return diff;
    }

    public static Date joinDateAndTime(Date alarmDate, Date alarmTime){
        Calendar cDate = Calendar.getInstance();
        cDate.setTime(alarmDate);

        Calendar cTime = Calendar.getInstance();
        cTime.setTime(alarmTime);

        cDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
        cDate.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));

        return cDate.getTime();
    }

    public static Date addDaysToDate(Date date, int days){
        if(date == null)
            date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }
}
