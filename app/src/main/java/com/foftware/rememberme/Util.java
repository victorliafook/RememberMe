package com.foftware.rememberme;

/**
 * Created by Victor on 29/07/2015.
 */
public class Util {
    public static String padInt(int inumber){
        String strInt;
        if(inumber < 10){
            strInt = "0" + inumber;
        }else{
            strInt = String.valueOf(inumber);
        }
        return strInt;
    }
}
