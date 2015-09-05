package com.foftware.rememberme;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Victor on 14/08/2015.
 */
public class RememberTaskFormDialog extends AlertDialog.Builder{
    private RememberTask task;
    private Context context;
    private View parentView;
    private View taskCrudView;
    private RememberTaskDAO datasource;
    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent, mLoggerReceiverPendingIntent;

    public RememberTaskFormDialog(View sourceView, RememberTask task, RememberTaskDAO datasource) {
            super(sourceView.getContext());
            setContext(sourceView.getContext());
            setDatasource(datasource);
            setTask(task);
            setParentView(sourceView);


        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            taskCrudView = inflater.inflate(R.layout.task_edit,null,false);

            final EditText editTextDate = (EditText) taskCrudView.findViewById(R.id.txtDate);
            final EditText editTextTime = (EditText) taskCrudView.findViewById(R.id.txtTime);
            final EditText editTextDesc = (EditText) taskCrudView.findViewById(R.id.txtDescription);
            final Switch switchAlarm = (Switch) taskCrudView.findViewById(R.id.swcAlarm);
            final RememberTask newTask = new RememberTask();

            editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        MainActivity mainActivity = (MainActivity) getParentView().getContext();
                        mainActivity.showDatePickerDialog(editTextDate);
                    }
                }
            });

            editTextTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        MainActivity mainActivity = (MainActivity) getParentView().getContext();
                        mainActivity.showTimePickerDialog(editTextTime);
                    }
                }
            });

            //TESTAR
            if(task.getId() > 0) {

                editTextDate.setText(((TextView) sourceView.findViewById(R.id.date)).getText().toString());
                editTextTime.setText(((TextView) sourceView.findViewById(R.id.time)).getText().toString());

                editTextDesc.setText(((TextView) sourceView.findViewById(R.id.description)).getText());
                switchAlarm.setChecked(((Switch) sourceView.findViewById(R.id.switchAlarm)).isChecked());

                this.setView(taskCrudView);
                this.setTitle(R.string.lbl_edit_task);
                this.setPositiveButton(R.string.lbl_save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SimpleDateFormat sdfDate = getDatasource().getDateFormat();
                                SimpleDateFormat sdfTime = getDatasource().getTimeFormat();
                                Date time,date;

                                try {
                                    time = sdfTime.parse(editTextTime.getText().toString());
                                    date =  sdfDate.parse(editTextDate.getText().toString());
                                    getTask().setDate(date);
                                    getTask().setTime(time);
                                    getTask().setDescription(editTextDesc.getText().toString());
                                    getTask().setAlarm(switchAlarm.isChecked());
                                    updateTask(getTask(), getContext());
                                    setAlarm(date, time);
                                    dialog.cancel();
                                } catch (ParseException e) {
                                    //TODO
                                    //in case of a invalid string
                                    Log.i("mine", e.getMessage());
                                }

                            }

                        })
                        .setNegativeButton(R.string.lbl_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }

                                })
                        .show();
            }else{
                setParentView((View) getParentView().getParent());

                this.setView(taskCrudView);
                this.setTitle(R.string.lbl_new_task);
                this.setPositiveButton(R.string.lbl_add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SimpleDateFormat sdfDate = getDatasource().getDateFormat();
                                SimpleDateFormat sdfTime = getDatasource().getTimeFormat();
                                Date time,date;
                                try {
                                    time = sdfTime.parse(editTextTime.getText().toString());
                                    date =  sdfDate.parse(editTextDate.getText().toString());
                                    newTask.setDate(date);
                                    newTask.setTime(time);
                                    newTask.setDescription(editTextDesc.getText().toString());
                                    newTask.setAlarm(switchAlarm.isChecked());
                                    saveNewTask(newTask, getContext());
                                    setAlarm(date, time);
                                    dialog.cancel();
                                } catch (ParseException e) {
                                    //TODO
                                    //in case of a invalid string
                                }

                            }

                        })
                        .setNegativeButton(R.string.lbl_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }

                                })
                        .show();

            }
    }

    private void setAlarm(Date alarmDate, Date alarmTime){
        Calendar cDate = Calendar.getInstance();
        cDate.setTime(alarmDate);

        Calendar cTime = Calendar.getInstance();
        cTime.setTime(alarmTime);

        cDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
        cDate.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));

        Long alarmLongTime = System.currentTimeMillis() + Util.dateDifference(new Date(), cDate.getTime());

        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getParentView().getContext().getSystemService(Context.ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(getParentView().getContext(),
                AlarmNotificationReceiver.class);
        mNotificationReceiverIntent.putExtra("alarmTime", alarmTime);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                getParentView().getContext(), 0, mNotificationReceiverIntent, 0);

        // Create an Intent to broadcast to the AlarmLoggerReceiver
        mLoggerReceiverIntent = new Intent(getParentView().getContext(),
                AlarmLoggerReceiver.class);

        // Create PendingIntent that holds the mLoggerReceiverPendingIntent
        mLoggerReceiverPendingIntent = PendingIntent.getBroadcast(
                getParentView().getContext(), 0, mLoggerReceiverIntent, 0);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + Util.dateDifference(new Date(), alarmDate),
                mNotificationReceiverPendingIntent);
    }

    /*TODO: usar este metodo para implementar o delete e o update dos alarms*/

    public boolean alarmExists(long alarmTime){
        Intent intent = new Intent("com.my.package.MY_UNIQUE_ACTION");
        intent.putExtra("alarmTime", alarmTime);

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

    private void saveNewTask(RememberTask task, Context context) {

        datasource.open();
        datasource.insertTask(task);


        ListActivity mainActivity = (ListActivity) getParentView().getContext();
        CustomListAdapter adapter = new CustomListAdapter(mainActivity, R.layout.list_item, datasource.getAllTasks());
        mainActivity.setListAdapter(adapter);
        mainActivity.getListView().invalidate();

    }

    private void updateTask(RememberTask task, Context context) {

        datasource.open();
        datasource.updateTask(task);
        ListActivity mainActivity = (ListActivity) getParentView().getContext();
        CustomListAdapter adapter = new CustomListAdapter(mainActivity, R.layout.list_item, datasource.getAllTasks());
        mainActivity.setListAdapter(adapter);
        mainActivity.getListView().invalidate();

    }

    public RememberTask getTask() {
        return task;
    }

    public void setTask(RememberTask task) {
        this.task = task;
    }

    public RememberTaskDAO getDatasource() {
        return datasource;
    }

    public void setDatasource(RememberTaskDAO datasource) {
        this.datasource = datasource;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public View getParentView() {
        return parentView;
    }

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }
}
