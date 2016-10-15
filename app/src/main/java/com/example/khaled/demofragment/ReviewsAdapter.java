package com.example.khaled.demofragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Khaled on 9/3/2016.
 */
public class ReviewsAdapter extends BaseAdapter {
    Context context;
    JSONObject jsonObject;
    int count;
    private static LayoutInflater inflater = null;
    public ReviewsAdapter(Context context, JSONObject jsonObject) {
        this.context = context;
        this.jsonObject = jsonObject;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageView.setImageResource(R.drawable.photo_video_start_icon);
        try {
            count = Integer.parseInt(jsonObject.getString("total_results"));
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
        View row;
        row = inflater.inflate(R.layout.review_row, null);

        TextView authorName ;
        TextView reviewContent;
        //ImageView imageView;
        authorName = (TextView) row.findViewById(R.id.text_view_review_author);
        reviewContent = (TextView) row.findViewById(R.id.text_view_review_content);
        //imageView = (ImageView) row.findViewById(R.id.imageView1);
        try {
            authorName.setText(jsonObject.getJSONArray("results").getJSONObject(position).getString("author"));
            reviewContent.setText(jsonObject.getJSONArray("results").getJSONObject(position).getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return row;    }
}
