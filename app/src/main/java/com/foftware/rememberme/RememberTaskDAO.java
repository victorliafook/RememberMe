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
            BdCore.TASK_DESCRIPTION, BdCore.TASK_TIME, BdCore.TASK_ALARM };
    private TimeZone systemTimeZone;
    private SimpleDateFormat dateFormat;

    public RememberTaskDAO(Context context){
        setDbHelper(new BdCore(context));
        setSystemTimeZone(TimeZone.getDefault());
        dateFormat = new SimpleDateFormat(BdCore.TIME_PATTERN);
        dateFormat.setTimeZone(getSystemTimeZone());
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

        Date time = task.getTime();

        ContentValues values = new ContentValues();
        values.put(BdCore.TASK_TIME, dateFormat.format(time));
        values.put(BdCore.TASK_DESCRIPTION, task.getDescription());
        values.put(BdCore.TASK_ALARM, (task.getAlarm().booleanValue() == true) ? 1 : 0);
        long insertId = database.insert(BdCore.TASKS_TABLE, null,
                values);
        Cursor cursor = database.query(BdCore.TASKS_TABLE,
                fields, BdCore.TASK_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cursor.close();
       }

    public List<RememberTask> getAllTasks() {
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

        try {
            date = getDateFormat().parse(cursor.getString(cursor.getColumnIndex(BdCore.TASK_TIME)));
        }catch(ParseException ex){
            date = new Date();
        }
        task.setTime(date);
        task.setAlarm((cursor.getInt(cursor.getColumnIndex(BdCore.TASK_ALARM)) == 1) ? true : false);
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
}
