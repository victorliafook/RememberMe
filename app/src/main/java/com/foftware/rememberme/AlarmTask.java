package com.foftware.rememberme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

/**
 * Created by Victor on 12/09/2015.
 */
public class AlarmTask {
    private AlarmManager alarmManager;
    private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent, mLoggerReceiverPendingIntent;

    public AlarmTask(AlarmManager alarmManager){
        this.setAlarmManager(alarmManager);
    }

    public void setAlarm(Context context, Date alarmDate, Date alarmTime, String alarmText){
        Date date = Util.joinDateAndTime(alarmDate,alarmTime);

        if(date.before(new Date()))
            return;

        Long alarmLongTime = System.currentTimeMillis() + Util.dateDifference(new Date(), date);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(context,
                AlarmNotificationReceiver.class);
        mNotificationReceiverIntent.putExtra("alarmTime", alarmLongTime);
        mNotificationReceiverIntent.putExtra("alarmText", alarmText);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                context, 0, mNotificationReceiverIntent, 0);

        // Create an Intent to broadcast to the AlarmLoggerReceiver
        mLoggerReceiverIntent = new Intent(context, AlarmLoggerReceiver.class);

        // Create PendingIntent that holds the mLoggerReceiverPendingIntent
        mLoggerReceiverPendingIntent = PendingIntent.getBroadcast(
                context, 0, mLoggerReceiverIntent, 0);

        getAlarmManager().set(AlarmManager.RTC_WAKEUP,
                alarmLongTime, mNotificationReceiverPendingIntent);
    }


    public boolean alarmExists(Context context, Date alarmDate, Date alarmTime){
        Date date = Util.joinDateAndTime(alarmDate,alarmTime);

        Long alarmLongTime = System.currentTimeMillis() + Util.dateDifference(new Date(), date);

        Intent intent = new Intent(context, AlarmNotificationReceiver.class);
        intent.putExtra("alarmTime", alarmLongTime);

        //getBroadcast with this FLAG_NO_CREATE will return null if the intent(and so the same alarm) already exists
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp)
        {
            Log.d(Util.debugTag, "Alarm is already active");
            return true;
        }
        return false;
    }

    public void cancelAlarm(Context context, Date alarmDate, Date alarmTime){
        Date date = Util.joinDateAndTime(alarmDate,alarmTime);

        Long alarmLongTime = System.currentTimeMillis() + Util.dateDifference(new Date(), date);


        Intent intent = new Intent(context, AlarmNotificationReceiver.class);
        intent.putExtra("alarmTime", alarmLongTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        getAlarmManager().cancel(pendingIntent);
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

    public void setAlarmManager(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }
}
