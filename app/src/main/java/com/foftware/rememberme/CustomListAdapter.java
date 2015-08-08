package com.foftware.rememberme;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Victor on 06/06/2015.
 */
public class CustomListAdapter extends ArrayAdapter<RememberTask> {

    private final Activity context;

    private Date date;

    private final List<RememberTask> tasks;

    public CustomListAdapter(Activity context, int resource, List<RememberTask> tasks) {
        super(context, resource, tasks);
        // TODO Auto-generated constructor stub

        this.context=context;
        //this.itemname=itemname;
        this.tasks=tasks;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        TextView txtDesc = (TextView) rowView.findViewById(R.id.description);
        TextView txtDate = (TextView) rowView.findViewById(R.id.date);
        TextView txtTime = (TextView) rowView.findViewById(R.id.time);
        Switch swcAlarm = (Switch) rowView.findViewById(R.id.switchAlarm);

        setDate(tasks.get(position).getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getDefault());

        txtDesc.setText(tasks.get(position).getDescription());
        txtDate.setText(sdf.format(tasks.get(position).getTime()));
        sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());

        txtTime.setText(sdf.format(tasks.get(position).getTime()));
        Boolean alarmOn = tasks.get(position).getAlarm();

        if(alarmOn != null)
            swcAlarm.setChecked(alarmOn.booleanValue());

        return rowView;

    };

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
