package com.foftware.rememberme;

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


/**
 * Created by Victor on 15/07/2015.
 */
public class RememberTaskDAO {
    private SQLiteDatabase database;
    private BdCore dbHelper;
    private String[] fields = { BdCore.TASK_ID,
            BdCore.TASK_DESCRIPTION, BdCore.TASK_TIME };

    public RememberTaskDAO(Context context){
        setDbHelper(new BdCore(context));
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void deleteTask(RememberTask task){
        database.delete(BdCore.TASKS_TABLE, BdCore.TASK_ID + "=" + task.getId(), null);
    }

    public List<RememberTask> getAllComments() {
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
        DateFormat format = new SimpleDateFormat(BdCore.TIME_PATTERN);
        try {
            date = format.parse(cursor.getString(cursor.getColumnIndex(BdCore.TASK_TIME)));
        }catch(ParseException ex){
            date = new Date();
        }
        task.setTime(date);
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
}
