package com.foftware.rememberme;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Victor on 21/07/2015.
 */
public class OnClickListenerCreateTask implements OnClickListener {
//    public static final String TASK_DATA_PICKED_INTENT_KEY = "DATA_PICKED_INTENT_KEY";
    private RememberTaskDAO datasource;
    private View v;
    private Context context;

    public OnClickListenerCreateTask(RememberTaskDAO datasource){
        this.datasource = datasource;
    }

    @Override
    public void onClick(View v) {
        if (context == null)
            context = v.getContext();
        this.v = v;

        new Thread(
            new Runnable() {
                public void run () {
                    if (datasource == null)
                        datasource = new RememberTaskDAO(context);
                    new RememberTaskFormDialog(getV(), new RememberTask(), datasource);
                }
            }
        ).start();


    }
    private View getV(){
        return this.v;
    }
}
