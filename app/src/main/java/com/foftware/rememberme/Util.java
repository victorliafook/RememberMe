package com.foftware.rememberme;

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
}
