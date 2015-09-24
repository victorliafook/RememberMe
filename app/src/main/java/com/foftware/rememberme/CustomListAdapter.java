package com.foftware.rememberme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
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
        // TODO Auto-generated constructor stub

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
        final Switch swcDone = (Switch) rowView.findViewById(R.id.switchDone);
        final Switch swcAlarm = (Switch) rowView.findViewById(R.id.switchAlarm);



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
            swcDone.setChecked(isDone.booleanValue());
        if(alarmOn != null)
            swcAlarm.setChecked(alarmOn.booleanValue());

        swcDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View viewTarget;
                viewTarget = v;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AlarmTask alarmTaskManager = ((MainActivity) context).getAlarmTaskManager();

                        Switch switchView = (Switch) viewTarget;
                        View rowView;
                        rowView = (View)viewTarget.getParent();
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
                            //TODO log
                        }

                        RememberTask task = new RememberTask();
                        task.setId(Long.parseLong(txtId.getText().toString()));
                        task.setDate(date);
                        task.setTime(time);
                        task.setAlarm(swcAlarm.isChecked());
                        task.setDescription(txtDesc.getText().toString());

                        if(switchView.isChecked()){
                            alarmTaskManager.setAlarm(context, date , time);
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
