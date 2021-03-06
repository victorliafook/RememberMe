package com.foftware.rememberme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Victor on 06/06/2015.
 */
public class CustomListAdapter extends ArrayAdapter<RememberTask> {

    private final Activity context;
    private RememberTaskDAO datasource;
    private Date date;
    private int index;
    private final List<RememberTask> tasks;

    public CustomListAdapter(Activity context, int resource, List<RememberTask> tasks) {
        super(context, resource, tasks);

        this.context = context;
        this.datasource = ((MainActivity) context).getDatasource();
        this.tasks = tasks;

    }

    public View getView(int position,View view,ViewGroup parent) {
        setIndex(position);
        RememberTask task = tasks.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null,true);

        TextView txtId = (TextView) rowView.findViewById(R.id.task_id);
        TextView txtDesc = (TextView) rowView.findViewById(R.id.description);
        TextView txtDateShow = (TextView) rowView.findViewById(R.id.dateShow);
        TextView txtDate = (TextView) rowView.findViewById(R.id.date);
        TextView txtTimeShow = (TextView) rowView.findViewById(R.id.timeShow);
        TextView txtTime = (TextView) rowView.findViewById(R.id.time);
        final CheckBox chkDone = (CheckBox) rowView.findViewById(R.id.chkDone);
        final CheckBox chkAlarm = (CheckBox) rowView.findViewById(R.id.chkAlarm);
        ImageView imgClock = (ImageView) rowView.findViewById(R.id.imgClock);



        setDate(task.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfDateShow = new SimpleDateFormat("dd MMM");
        sdf.setTimeZone(TimeZone.getDefault());

        txtId.setText(Long.toString(task.getId()));
        txtDesc.setText(task.getDescription());
        txtDateShow.setText(sdfDateShow.format(task.getDate()));
        txtDate.setText(sdf.format(task.getDate()));
        sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        txtTimeShow.setText(sdf.format(task.getTime()));
        txtTime.setText(sdf.format(task.getTime()));
        Boolean isDone = task.getDone();
        final Boolean alarmOn = task.getAlarm();

        if(isDone != null)
            chkDone.setChecked(isDone.booleanValue());
        if(alarmOn != null) {
            chkAlarm.setChecked(alarmOn.booleanValue());
            if(alarmOn.booleanValue()) {
                imgClock.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_action_alarms_on));
            }else {
                imgClock.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_action_alarms));
            }
        }

        chkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View viewTarget;
                viewTarget = v;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AlarmTask alarmTaskManager = ((MainActivity) context).getAlarmTaskManager();

                        CheckBox checkView = (CheckBox) viewTarget;
                        View rowView;
                        rowView = (View)viewTarget.getParent().getParent();
                        TextView txtId = (TextView) rowView.findViewById(R.id.task_id);
                        TextView txtDate = (TextView) rowView.findViewById(R.id.date);
                        TextView txtTime = (TextView) rowView.findViewById(R.id.time);
                        TextView txtDesc = (TextView) rowView.findViewById(R.id.description);
                        SimpleDateFormat sdfDate = datasource.getDateFormat();
                        SimpleDateFormat sdfTime = datasource.getTimeFormat();
                        Date date = new Date();
                        Date time = new Date();
                        try{
                            date = sdfDate.parse(txtDate.getText().toString());
                            time = sdfTime.parse(txtTime.getText().toString());
                        }catch(Exception e){
                            Log.i(Util.debugTag, e.getMessage());
                        }

                        RememberTask task = new RememberTask();
                        task.setId(Long.parseLong(txtId.getText().toString()));
                        task.setDate(date);
                        task.setTime(time);
                        task.setAlarm(chkAlarm.isChecked());
                        task.setDescription(txtDesc.getText().toString());

                        if(checkView.isChecked()){
                            alarmTaskManager.setAlarm(context, date , time, txtDesc.getText().toString());
                            task.setDone(true);
                            datasource.updateTask(task);
                        }else{
                            alarmTaskManager.cancelAlarm(context, date , time);
                            task.setDone(false);
                            datasource.updateTask(task);
                        }

                    }
                }).start();


            }
        });

        return rowView;

    };

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return this.index;
    }
}
