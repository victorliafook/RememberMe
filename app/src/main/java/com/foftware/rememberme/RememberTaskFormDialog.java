package com.foftware.rememberme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Victor on 14/08/2015.
 */
public class RememberTaskFormDialog extends AlertDialog.Builder{
    private RememberTask task;
    private Context context;
    private View parentView;
    private View taskCrudView;
    private RememberTaskDAO datasource;
    private AlarmTask mAlarmTaskManager;

    private RememberTask myTask;

    private EditText txtDate;
    private EditText txtTime;

    //dateSet is used to always insert the task's date into the bank.
    private Date dateSet;

    private int mMinute;
    private int mHour;
    private int mDay;
    private int mMonth;
    private int mYear;

    public RememberTaskFormDialog(View sourceView, RememberTask task, final RememberTaskDAO datasource) {
            super(sourceView.getContext());
            setContext(sourceView.getContext());
            setDatasource(datasource);
            setTask(task);
            setParentView(sourceView);
            myTask = task;
            mAlarmTaskManager = ((MainActivity) getParentView().getContext()).getAlarmTaskManager();

            sourceView.post(
                new Runnable() {
                    @Override
                    public void run() {

                        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        taskCrudView = inflater.inflate(R.layout.task_edit,null,false);

                        txtDate = (EditText) taskCrudView.findViewById(R.id.txtDate);
                        txtTime = (EditText) taskCrudView.findViewById(R.id.txtTime);
                        final EditText editTextDesc = (EditText) taskCrudView.findViewById(R.id.txtDescription);


                        final CheckBox chkEditAlarm = (CheckBox) taskCrudView.findViewById(R.id.chkEditAlarm);
                        final CheckBox chkEditDone = new CheckBox(getContext());
                        final RememberTask newTask = new RememberTask();

                        /** ######################################################################### **/
                        /** setting editTexts not editable therefore only accessible from the pickers **/
                        /** ######################################################################### **/
                        txtDate.setKeyListener(null);
                        txtTime.setKeyListener(null);

                        txtDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick (View v) {
                                showDatePickerDialog(txtDate);

                            }
                        });

                        txtTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick (View v) {
                                showTimePickerDialog(txtTime);

                            }
                        });

                        /** ################################################################**/
                        /** id greater than zero means that we are editing an existing task **/
                        /** ################################################################**/
                        if(myTask.getId() > 0) {


                             try{
                                dateSet = datasource.getDateFormat().parse(((TextView) getParentView().findViewById(R.id.date)).getText().toString());
                            }catch(Exception e){
                                dateSet = new Date();
                            }

                            setTxtDateText(dateSet);

                            txtTime.setText(((TextView) getParentView().findViewById(R.id.time)).getText().toString());

                            editTextDesc.setText(((TextView) getParentView().findViewById(R.id.description)).getText());
                            chkEditAlarm.setChecked(((CheckBox) getParentView().findViewById(R.id.chkAlarm)).isChecked());
                            chkEditDone.setChecked(((CheckBox) getParentView().findViewById(R.id.chkDone)).isChecked());

                            setView(taskCrudView);
                            setTitle(R.string.lbl_edit_task);
                            setPositiveButton(R.string.lbl_save,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            if(txtDate.getText().toString().equals("") || txtTime.getText().toString().equals("") ||
                                                    editTextDesc.getText().toString().equals("")){

                                                showToast(R.string.msgInvalidFields, Toast.LENGTH_LONG);
                                            }else {
                                                SimpleDateFormat sdfDate = getDatasource().getDateFormat();
                                                SimpleDateFormat sdfTime = getDatasource().getTimeFormat();
                                                Date time;

                                                try {
                                                    time = sdfTime.parse(txtTime.getText().toString());
                                                    getTask().setDate(dateSet);
                                                    getTask().setTime(time);
                                                    getTask().setDescription(editTextDesc.getText().toString());
                                                    getTask().setAlarm(chkEditAlarm.isChecked());
                                                    getTask().setDone(chkEditDone.isChecked());
                                                    updateTask(getTask(), getContext());
                                                    if (chkEditAlarm.isChecked()) {
                                                        mAlarmTaskManager.setAlarm(getContext(), dateSet, time, editTextDesc.getText().toString());
                                                    } else {
                                                        if (mAlarmTaskManager.alarmExists(getContext(), dateSet, time)) {
                                                            mAlarmTaskManager.cancelAlarm(getContext(), dateSet, time);
                                                        }
                                                    }
                                                    dialog.cancel();
                                                } catch (ParseException e) {
                                                    //TODO
                                                    //in case of a invalid string
                                                    showToast(R.string.msgInvalidDate, Toast.LENGTH_LONG);
                                                    Log.i("mine", e.getMessage());
                                                }
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
                        /** #############################################################**/
                        /** on the other hand... id greater than zero means a new task **/
                        /** #############################################################**/
                        }else{

                            setParentView((View) getParentView().getParent());
                            setView(taskCrudView);
                            setTitle(R.string.lbl_new_task);
                            setPositiveButton(R.string.lbl_add,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if(txtDate.getText().toString().equals("") || txtTime.getText().toString().equals("") ||
                                                    editTextDesc.getText().toString().equals("")){

                                                showToast(R.string.msgInvalidFields, Toast.LENGTH_LONG);

                                            }else {

                                                SimpleDateFormat sdfTime = getDatasource().getTimeFormat();
                                                Date time;
                                                try {
                                                    time = sdfTime.parse(txtTime.getText().toString());

                                                    newTask.setDate(dateSet);
                                                    newTask.setTime(time);
                                                    newTask.setDescription(editTextDesc.getText().toString());
                                                    newTask.setDone(false);
                                                    newTask.setAlarm(chkEditAlarm.isChecked());
                                                    saveNewTask(newTask, getContext());
                                                    if (chkEditAlarm.isChecked())
                                                        mAlarmTaskManager.setAlarm(getContext(), dateSet, time, editTextDesc.getText().toString());
                                                    dialog.cancel();
                                                } catch (ParseException e) {
                                                    //TODO
                                                    //in case of a invalid string
                                                    showToast(R.string.msgInvalidDate, Toast.LENGTH_LONG);
                                                }
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
                }

            );




    }

    public void showToast(int message, int length){
        Toast toast = Toast.makeText(getContext(), getContext().getString(message), length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    private void saveNewTask(RememberTask task, Context context) {
        //datasource.open();
        datasource.insertTask(task);

        MainActivity mainActivity = (MainActivity) getParentView().getContext();
        mainActivity.updateList(datasource.getTasksByDate(mainActivity.getDateSelected()));
        showToast(R.string.msgNewTaskSaved, Toast.LENGTH_LONG);

    }

    private void updateTask(RememberTask task, Context context) {

        //datasource.open();
        datasource.updateTask(task);

        MainActivity mainActivity = (MainActivity) getParentView().getContext();
        mainActivity.updateList(datasource.getTasksByDate(mainActivity.getDateSelected()));
        showToast(R.string.msgTaskSaved, Toast.LENGTH_LONG);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        txtTime.setText(new StringBuilder().append(Util.padInt(mHour)).append(":").append(Util.padInt(mMinute)));

    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;

        try{
            dateSet = datasource.getDateFormat().parse(mYear + "-" + mMonth + "-" + mDay);
        }catch(Exception e){
            dateSet = new Date();
        }
        setTxtDateText(dateSet);
    }

    public void showDatePickerDialog(View v) {
        //txtDate = (EditText) v;
        Date initialDate = null;
        try{
            initialDate = datasource.getDateFormat().parse(((EditText) v).getText().toString());
        }catch(Exception e){

        }
        DialogFragment newFragment = new DatePickerFragment(this, initialDate);
        MainActivity mainActivity = (MainActivity) getParentView().getContext();
        newFragment.show(mainActivity.getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        //txtTime = (EditText) v;
        DialogFragment newFragment = new TimePickerFragment(this);
        MainActivity mainActivity = (MainActivity) getParentView().getContext();
        newFragment.show(mainActivity.getFragmentManager(), "timePicker");
    }

    public void setTxtDateText(Date date){
        java.text.DateFormat dateFormatter;
        dateFormatter = java.text.DateFormat.getDateInstance(java.text.DateFormat.DEFAULT, Locale.getDefault());

        txtDate.setText(dateFormatter.format(date));
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private RememberTaskFormDialog dialog;
        private Date initialDate;

        public DatePickerFragment(RememberTaskFormDialog dialog, Date date){
            this.dialog = dialog;
            this.initialDate = date;
        }

        /*public DatePickerFragment(RememberTaskFormDialog dialog){
            this.dialog = dialog;

        }*/

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;
            final Calendar c = Calendar.getInstance();

            if(this.initialDate != null) {
                c.setTime(this.initialDate);
            }
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dialog.onDateSet(view, year, month + 1, day);

        }

    }
    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private RememberTaskFormDialog dialog;
        public TimePickerFragment(RememberTaskFormDialog dialog){
            this.dialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dialog.onTimeSet(view, hourOfDay,
                    minute);
            //Log.i("MEUDEBUG",String.valueOf(hourOfDay) +  ":" + minute);
        }

       /* @Override
        public void onDismiss(DialogInterface dialog){

        }*/
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
