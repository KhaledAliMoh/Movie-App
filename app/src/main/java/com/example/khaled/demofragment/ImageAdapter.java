package com.example.khaled.demofragment;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Khaled on 9/1/2016.
 */
public class ImageAdapter extends BaseAdapter {
    final static String BASE_URL = "http://image.tmdb.org/t/p/w185/";
    Context context;
    ArrayList<String> list;
    public ImageAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            //imageView.getLayoutParams().height = 20;
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                imageView.setLayoutParams(new GridView.LayoutParams(220, 300));
            else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                imageView.setLayoutParams(new GridView.LayoutParams(450, 700));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        }
        else {
            imageView = (ImageView) convertView;
        }

        //imageView.setImageResource(list.get(position));


        Picasso.with(context).load(BASE_URL + list.get(position)).into(imageView);

        return imageView;
    }
}
