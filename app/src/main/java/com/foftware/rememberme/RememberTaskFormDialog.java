package com.foftware.rememberme;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public RememberTaskFormDialog(View sourceView, RememberTask task, RememberTaskDAO datasource) {
            super(sourceView.getContext());
            setContext(sourceView.getContext());
            setDatasource(datasource);

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            taskCrudView = inflater.inflate(R.layout.task_edit,null,false);


            final EditText editTextTime = (EditText) taskCrudView.findViewById(R.id.txtTime);
            final EditText editTextDesc = (EditText) taskCrudView.findViewById(R.id.txtDescription);
            final Switch switchAlarm = (Switch) taskCrudView.findViewById(R.id.swcAlarm);
            final RememberTask newTask = new RememberTask();

            //TESTAR
            if(task.getId() > 0) {

                String strDateTime = "";
                strDateTime = strDateTime.concat(((TextView) sourceView.findViewById(R.id.date)).getText().toString());
                strDateTime = strDateTime.concat(" ");
                strDateTime = strDateTime.concat(((TextView) sourceView.findViewById(R.id.time)).getText().toString());

                editTextTime.setText(strDateTime);
                editTextDesc.setText(((TextView) sourceView.findViewById(R.id.description)).getText());
                switchAlarm.setChecked(((Switch) sourceView.findViewById(R.id.switchAlarm)).isChecked());

                this.setView(taskCrudView);
                this.setTitle(R.string.lbl_edit_task);
                this.setPositiveButton(R.string.lbl_save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SimpleDateFormat sdf = getDatasource().getDateFormat();
                                Date time;

                                try {
                                    time = sdf.parse(editTextTime.getText().toString());
                                    getTask().setTime(time);
                                    getTask().setDescription(editTextDesc.getText().toString());
                                    getTask().setAlarm(switchAlarm.isChecked());
                                    updateTask(getTask(), getContext());
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
                this.setView(taskCrudView);
                this.setTitle(R.string.lbl_new_task);
                this.setPositiveButton(R.string.lbl_add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SimpleDateFormat sdf = getDatasource().getDateFormat();
                                Date time;
                                try {
                                    time = sdf.parse(editTextTime.getText().toString());
                                    newTask.setTime(time);
                                    newTask.setDescription(editTextDesc.getText().toString());
                                    newTask.setAlarm(switchAlarm.isChecked());
                                    saveNewTask(newTask, getContext());
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

    private void saveNewTask(RememberTask task, Context context) {

        datasource.open();
        datasource.insertTask(task);

        parentView = (View)parentView.getParent();
        ListActivity mainActivity = (ListActivity) parentView.getContext();
        CustomListAdapter adapter = new CustomListAdapter(mainActivity, R.layout.list_item, datasource.getAllTasks());
        mainActivity.setListAdapter(adapter);
        mainActivity.getListView().invalidate();

    }

    private void updateTask(RememberTask task, Context context) {

        datasource.open();
        datasource.updateTask(task);
        ListActivity mainActivity = (ListActivity) parentView.getContext();
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
}
