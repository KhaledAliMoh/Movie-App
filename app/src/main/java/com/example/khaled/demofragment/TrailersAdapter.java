package com.example.khaled.demofragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Khaled on 9/3/2016.
 */
public class TrailersAdapter extends BaseAdapter {
    Context context;
    JSONObject jsonObject;
    int count;
    private static LayoutInflater inflater = null;



    public TrailersAdapter(Context context, JSONObject jsonObject) {
        this.context = context;
        this.jsonObject = jsonObject;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageView.setImageResource(R.drawable.photo_video_start_icon);
        try {
            count = jsonObject.getJSONArray("results").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return count;
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
        // Error is here
        View row;
        row = inflater.inflate(R.layout.trailer_row, null);

        TextView trailerName ;
        //ImageView imageView;
        trailerName = (TextView) row.findViewById(R.id.text_view_trailer_name);
        //imageView = (ImageView) row.findViewById(R.id.imageView1);
        try {
            trailerName.setText(jsonObject.getJSONArray("results").getJSONObject(position).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return row;
    }
}
