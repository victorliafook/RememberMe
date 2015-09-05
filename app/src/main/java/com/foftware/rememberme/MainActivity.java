package com.foftware.rememberme;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class MainActivity extends ListActivity implements
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private RememberTaskDAO datasource;
    View editView;
    private TextView txtDate;
    private TextView txtTime;
    private int mMinute;
    private int mHour;
    private int mDay;
    private int mMonth;
    private int mYear;
    private enum contextOptions{
        Edit,Delete;
    }
    private OnClickListenerCreateTask dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        datasource = new RememberTaskDAO(this);
        datasource.open();
        setContentView(R.layout.activity_main);

        final Button buttonCreateTask = (Button) findViewById(R.id.button_create_task);

        dialog = new OnClickListenerCreateTask();

        buttonCreateTask.setOnClickListener(dialog);

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.list_item, datasource.getAllTasks());
        setListAdapter(adapter);

        ListView lv = getListView();
        //lv.setClickable(true);
        registerForContextMenu(lv);
        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(final ContextMenu menu,
                                            final View v, final ContextMenu.ContextMenuInfo menuInfo) {
                if (v.getId()== getListView().getId()) {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
                    //menu.setHeaderTitle(Countries[info.position]);
                    String[] menuItems = getResources().getStringArray(R.array.contextmenu_items);
                    for (int i = 0; i<menuItems.length; i++) {
                        menu.add(Menu.NONE, i, i, menuItems[i]);

                    }
                }
            }
        });
        // Enable filtering when the user types in the virtual keyboard
        lv.setTextFilterEnabled(true);
    }

    // Callback called when user sets the time
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        txtTime.setText(new StringBuilder().append(Util.padInt(mHour)).append(":").append(Util.padInt(mMinute)));

    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        txtDate.setText(new StringBuilder().append(mYear).append("-").append(Util.padInt(mMonth)).append("-").append(Util.padInt(mDay)));


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        String[] items = getResources().getStringArray(R.array.contextmenu_items);
        RememberTask task = null;
        CustomListAdapter adapter = (CustomListAdapter)getListAdapter();
        //ArrayAdapter<RememberTask> adapter = (ArrayAdapter<RememberTask>) getListAdapter();

        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout itemView = (LinearLayout)inflater.inflate(R.layout.list_item, null);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        itemView = (LinearLayout)info.targetView;
        //LinearLayout itemView = (LinearLayout)info.targetView;

        switch (contextOptions.valueOf(item.getTitle().toString())){
            case Edit:
                task = adapter.getItem(index);
                //new OnClickListenerCreateTask().onClick(itemView);
                new RememberTaskFormDialog(itemView, task, datasource);
                break;
            case Delete:
                if (getListAdapter().getCount() > 0) {
                    task = adapter.getItem(index);
                    datasource.deleteTask( task.getId() );
                    adapter.remove(task);
                }

                break;
            default:

        }
        return true;
    }

    /*public void onCreateContextMenu(final ContextMenu menu,
                                    final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()== getListView().getId()) {

        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){

        super.onResume();
    }

    public void showDatePickerDialog(View v) {
        txtDate = (EditText) v;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        txtTime = (EditText) v;
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

       // private EditText txtDateTime;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

           /* LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.task_edit, null);
            txtDateTime = (EditText) view.findViewById(R.id.txtTime);
            */
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }




        public void onDateSet(DatePicker view, int year, int month, int day) {
           ((DatePickerDialog.OnDateSetListener) getActivity()).onDateSet(view, year, month + 1, day);

        }

    }
    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        //EditText txtDateTime;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

          /*  LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.task_edit, null);
            txtDateTime = (EditText) view.findViewById(R.id.txtTime);
            */
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ((TimePickerDialog.OnTimeSetListener) getActivity()).onTimeSet(view, hourOfDay,
                    minute);
           //Log.i("MEUDEBUG",String.valueOf(hourOfDay) +  ":" + minute);
        }

        /*@Override
        public void onDismiss(DialogInterface dialog){
            CustomListAdapter adapter = new CustomListAdapter(getActivity(), R.layout.list_item, datasource.getAllTasks());
            ((ListActivity)getActivity()).setListAdapter(adapter);
            ((ListActivity)getActivity()).getListView().invalidate();
        }*/
    }
}
