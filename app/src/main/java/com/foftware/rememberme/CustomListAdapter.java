package com.foftware.rememberme;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Victor on 06/06/2015.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private final String[] images;
    private final String dir;
    public CustomListAdapter(Activity context, String[] images, String dir) {
        super(context, R.layout.list_item, images);
        // TODO Auto-generated constructor stub

        this.context=context;
        //this.itemname=itemname;
        this.images=images;
        this.dir = dir;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        //Uri imageUri =  new Uri

        txtTitle.setText(images[position]);
        imageView.setImageURI(Uri.fromFile(new File(dir + "/" + images[position])));

        return rowView;

    };
}
