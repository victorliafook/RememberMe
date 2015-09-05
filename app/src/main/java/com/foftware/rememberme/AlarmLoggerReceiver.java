package com.foftware.rememberme;

import java.text.DateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Victor on 17/08/2015.
 */
public class AlarmLoggerReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        // Log receipt of the Intent with timestamp
        Log.i(Util.debugTag,"Logging alarm at:" + DateFormat.getDateTimeInstance().format(new Date()));

    }

}
