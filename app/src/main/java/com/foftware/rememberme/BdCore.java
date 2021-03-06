package com.foftware.rememberme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Victor on 15/07/2015.
 */
public class BdCore extends SQLiteOpenHelper{
    private static final String BD_NAME = "rememberMe";
    public static final String TASKS_TABLE = "tasks";
    public static final String TASK_ID = "_id";
    public static final String TASK_DESCRIPTION = "txt_description";
    public static final String TASK_DATE = "i_date";
    public static final String TASK_TIME = "i_time";
    public static final String TASK_ALARM = "i_alarm";
    public static final String TASK_DONE = "i_done";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm";
    private static final int VERSION = 4;

    // Database creation statement
    private static final String DATABASE_CREATE = "create table "
            + TASKS_TABLE + "(" + "_id"
            + " integer primary key autoincrement, " + TASK_DESCRIPTION
            + " text not null," + TASK_DATE
            + " text not null," + TASK_TIME
            + " text not null, " + TASK_ALARM
            + " integer not null," + TASK_DONE
            + " integer not null"
            + ");";

    public BdCore(Context context){
        super(context, BD_NAME, null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BdCore.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + "tasks");
        onCreate(db);
    }


}
