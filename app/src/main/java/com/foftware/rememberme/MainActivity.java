package com.foftware.rememberme;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;

import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends ListActivity{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private TextView txtNoTasks;
    private TextView txtCurrentDate;
    private RememberTaskDAO datasource;
    private AlarmTask alarmTaskManager;
    private Date dateSelected = new Date();
    private GestureDetector gestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent e){
        return getListView().dispatchTouchEvent(e);

    }

    private enum contextOptions{
        Edit,Delete;
    }
    private OnClickListenerCreateTask dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDatasource(new RememberTaskDAO(this));
        getDatasource().open();
        setAlarmTaskManager(new AlarmTask((AlarmManager)this.getSystemService(Context.ALARM_SERVICE)));
        setContentView(R.layout.activity_main);
        //gestureDetector = new GestureDetector(this, this);


        final Button buttonCreateTask = (Button) findViewById(R.id.button_create_task);
        setTxtNoTasks((TextView)findViewById(R.id.txtNoTasks));
        setTxtCurrentDate((TextView)findViewById(R.id.txtCurrentDate));

        dialog = new OnClickListenerCreateTask(getDatasource());

        buttonCreateTask.setOnClickListener(dialog);

        new Thread(new Runnable(){
            public void run(){
                List<RememberTask> taskList;
                taskList = getDatasource().getTasksByDate(getDateSelected());
                if(taskList.size() < 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(R.string.msgNoTasksToday, Toast.LENGTH_LONG);
                        }
                    });
                }
                updateList(taskList);

            }
        }).start();


        ListView lv = getListView();
        lv.setOnTouchListener(new SwipeGestureListener(MainActivity.this));
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

        LayoutInflater inflater = this.getLayoutInflater();
        LinearLayout itemView
                ;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        itemView = (LinearLayout)info.targetView;

        switch (contextOptions.valueOf(item.getTitle().toString())){
            case Edit:
                task = adapter.getItem(index);

                //new OnClickListenerCreateTask().onClick(itemView);
                new RememberTaskFormDialog(itemView, task, getDatasource());
                break;
            case Delete:
                if (getListAdapter().getCount() > 0) {
                    task = adapter.getItem(index);
                    getDatasource().deleteTask(task.getId());
                    adapter.remove(task);
                }

                break;
            default:

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_viewall:
                setDateSelected(null);
                List<RememberTask> list = getDatasource().getTasksByDate(getDateSelected());
                updateList(list);

                break;
            case R.id.action_settings:
                break;
            case R.id.action_quit:
                System.exit(1);
                break;
            case R.id.action_pickday:
                DialogFragment newFragment = new DateDialog();
                newFragment.show(getFragmentManager(), "dateDialog");
                break;
            default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }
    //TODO
    public void onDatePickSet(int year, int month, int day){

        SimpleDateFormat sdf = getDatasource().getDateFormat();
        try {
            setDateSelected(sdf.parse("" + year + "-" + month + "-" + day));
        }catch(Exception e){
            setDateSelected(new Date());
        }
        List<RememberTask> list = getDatasource().getTasksByDate(getDateSelected());
        if(list.size() < 1){
            showToast(R.string.msgNoTasksThisDate, Toast.LENGTH_LONG);
        }
        updateList(list);
    }

    @Override
    protected void onResume(){

        super.onResume();
    }

    public void showToast(int message, int length){
        Toast toast = Toast.makeText(this.getApplicationContext(), getString(message), length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    public void updateList(List<RememberTask> list){
        Date currentDate = getDateSelected();
        setTxtCurrentDate(getDateSelected());
        if(list == null)
            list = getDatasource().getTasksByDate(currentDate);
        if(list.size() == 0) {
            getTxtNoTasks().setVisibility(View.VISIBLE);

        }else{
            getTxtNoTasks().setVisibility(View.INVISIBLE);
        }
        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.list_item, list);
        this.setListAdapter(adapter);
        this.getListView().invalidate();
    }

    public TextView getTxtNoTasks() {
        return txtNoTasks;
    }

    public void setTxtNoTasks(TextView txtNoTasks) {
        this.txtNoTasks = txtNoTasks;
    }

    public TextView getTxtCurrentDate() {
        return txtCurrentDate;
    }

    public void setTxtCurrentDate(TextView txtCurrentDate) {
        this.txtCurrentDate = txtCurrentDate;
    }

    public void setTxtCurrentDate(Date date) {
        if(date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
            this.txtCurrentDate.setText(sdf.format(date));
        }else {
            this.txtCurrentDate.setText("");
        }
    }

    public Date getDateSelected() {
        return dateSelected;
    }

    public void setDateSelected(Date dateSelected) {
        this.dateSelected = dateSelected;
    }

    public RememberTaskDAO getDatasource() {
        return datasource;
    }

    public void setDatasource(RememberTaskDAO datasource) {
        this.datasource = datasource;
    }

    public AlarmTask getAlarmTaskManager() {
        return alarmTaskManager;
    }

    public void setAlarmTaskManager(AlarmTask mAlarmTaskManager) {
        this.alarmTaskManager = mAlarmTaskManager;
    }


    @SuppressLint("ValidFragment")
    public class DateDialog extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((MainActivity) getActivity()).onDatePickSet(year, month + 1, day);

        }

    }

    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener implements
            View.OnTouchListener {
        Context context;
        GestureDetector gDetector;

        static final int SWIPE_MIN_DISTANCE = 120;
        static final int SWIPE_MAX_OFF_PATH = 250;
        static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public SwipeGestureListener() {
            super();
        }

        public SwipeGestureListener(Context context) {
            this(context, null);
        }

        public SwipeGestureListener(Context context, GestureDetector gDetector) {

            if (gDetector == null)
                gDetector = new GestureDetector(context, this);

            this.context = context;
            this.gDetector = gDetector;

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            //String countryName = (String) targetView.getItemAtPosition(position);

            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
                        || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                    return false;
                }
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {

                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {

                }
            } else {
                if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                    return false;
                }
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                    //rtl
                    MainActivity mainActivity = (MainActivity)context;
                    mainActivity.setDateSelected(Util.addDaysToDate(mainActivity.getDateSelected(), 1));
                    mainActivity.updateList(null);
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                    //ltr
                    MainActivity mainActivity = (MainActivity)context;
                    mainActivity.setDateSelected(Util.addDaysToDate(mainActivity.getDateSelected(), -1));
                    mainActivity.updateList(null);
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return gDetector.onTouchEvent(event);
        }

        public GestureDetector getDetector() {
            return gDetector;
        }

    }

}
