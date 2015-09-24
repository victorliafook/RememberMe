package com.foftware.rememberme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by Victor on 15/07/2015.
 */
public class RememberTaskDAO {
    private SQLiteDatabase database;
    private BdCore dbHelper;
    private String[] fields = { BdCore.TASK_ID,
            BdCore.TASK_DESCRIPTION, BdCore.TASK_DATE, BdCore.TASK_TIME, BdCore.TASK_ALARM, BdCore.TASK_DONE };
    private TimeZone systemTimeZone;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public RememberTaskDAO(Context context){
        setDbHelper(new BdCore(context));
        setSystemTimeZone(TimeZone.getDefault());
        dateFormat = new SimpleDateFormat(BdCore.DATE_PATTERN);
        dateFormat.setTimeZone(getSystemTimeZone());

        timeFormat = new SimpleDateFormat(BdCore.TIME_PATTERN);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void deleteTask(Long taskId){
        database.delete(BdCore.TASKS_TABLE, BdCore.TASK_ID + "=" + taskId, null);
    }

    public void insertTask(RememberTask task) {
        Date date = task.getDate();
        Date time = task.getTime();

        ContentValues values = new ContentValues();
        values.put(BdCore.TASK_DATE, dateFormat.format(date));
        values.put(BdCore.TASK_TIME, timeFormat.format(time));
        values.put(BdCore.TASK_DESCRIPTION, task.getDescription());
        values.put(BdCore.TASK_ALARM, (task.getAlarm().booleanValue() == true) ? 1 : 0);
        values.put(BdCore.TASK_DONE, (task.getDone().booleanValue() == true) ? 1 : 0);
        long insertId = database.insert(BdCore.TASKS_TABLE, null,
                values);
        Cursor cursor = database.query(BdCore.TASKS_TABLE,
                fields, BdCore.TASK_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
       }

    public void updateTask(RememberTask task) {
        Date date = task.getDate();
        Date time = task.getTime();

        ContentValues values = new ContentValues();
        values.put(BdCore.TASK_DATE, dateFormat.format(date));
        values.put(BdCore.TASK_TIME, timeFormat.format(time));
        values.put(BdCore.TASK_DESCRIPTION, task.getDescription());
        values.put(BdCore.TASK_ALARM, (task.getAlarm().booleanValue() == true) ? 1 : 0);
        values.put(BdCore.TASK_DONE, (task.getDone().booleanValue() == true) ? 1 : 0);
        long rowsAffected = database.update(BdCore.TASKS_TABLE, values, BdCore.TASK_ID + "=" + (int)task.getId(), null );

        Cursor cursor = database.query(BdCore.TASKS_TABLE,
                fields, BdCore.TASK_ID + " = " + task.getId(), null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
    }

    public List<RememberTask> getAllTasks(Boolean order) {
        List<RememberTask> tasks = new ArrayList<RememberTask>();

        Cursor cursor = database.query(BdCore.TASKS_TABLE,
                fields, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RememberTask task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        if(order){
            Collections.sort(tasks);
        }

        return tasks;
    }

    public List<RememberTask> getTasksByDate(Date date){
        List<RememberTask> tasks = new ArrayList<RememberTask>();

        if(date == null) {
            tasks = getAllTasks(true);
            return tasks;
        }

        Cursor cursor = database.query(BdCore.TASKS_TABLE,
                fields, BdCore.TASK_DATE + "= ?", new String[] {dateFormat.format(date)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RememberTask task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return tasks;
    }

    /*TODO: fazer direito, retornando so com o date do dia*/
    public List<RememberTask> getTodaysTasks() {
        List<RememberTask> tasks = new ArrayList<RememberTask>();

        Cursor cursor = database.query(BdCore.TASKS_TABLE,
                fields, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RememberTask task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return tasks;
    }

    private RememberTask cursorToTask(Cursor cursor) {
        RememberTask task = new RememberTask();
        task.setId(cursor.getLong(cursor.getColumnIndex(BdCore.TASK_ID)));
        task.setDescription(cursor.getString(cursor.getColumnIndex(BdCore.TASK_DESCRIPTION)));

        Date date;
        Date time;

        try {
            date = getDateFormat().parse(cursor.getString(cursor.getColumnIndex(BdCore.TASK_DATE)));
            time = getTimeFormat().parse(cursor.getString(cursor.getColumnIndex(BdCore.TASK_TIME)));
        }catch(ParseException ex){
            date = new Date();
            time = new Date();
        }

        task.setDate(date);
        task.setTime(time);
        task.setAlarm((cursor.getInt(cursor.getColumnIndex(BdCore.TASK_ALARM)) == 1) ? true : false);
        task.setDone((cursor.getInt(cursor.getColumnIndex(BdCore.TASK_DONE)) == 1) ? true : false);
        return task;
    }


    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public BdCore getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(BdCore dbHelper) {
        this.dbHelper = dbHelper;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public TimeZone getSystemTimeZone() {
        return systemTimeZone;
    }

    public void setSystemTimeZone(TimeZone systemTimeZone) {
        this.systemTimeZone = systemTimeZone;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public SimpleDateFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(SimpleDateFormat timeFormat) {
        this.timeFormat = timeFormat;
    }
}
