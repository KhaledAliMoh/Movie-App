package com.example.khaled.demofragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Khaled on 9/5/2016.
 */
public class ImageAdapterDB extends BaseAdapter {
    Context context;
    ArrayList<Integer> idsList;
    private DatabaseHelper mydb;

    public ImageAdapterDB(Context context, ArrayList<Integer> idsList) {
        this.context = context;
        this.idsList = idsList;
        mydb = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return idsList.size();
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
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                imageView.setLayoutParams(new GridView.LayoutParams(220, 300));
            else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                imageView.setLayoutParams(new GridView.LayoutParams(400, 700));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        }
        else
        {
            imageView = (ImageView) convertView;
        }

        Bitmap posterBitmap = getImageFromDB(position);
        imageView.setImageBitmap(posterBitmap);

        return imageView;
    }

    private Bitmap getImageFromDB(int position){
        byte[] posterByteArray = mydb.getPoster(idsList.get(position));
        return BitmapFactory.decodeByteArray(posterByteArray, 0, posterByteArray.length);
    }
}
