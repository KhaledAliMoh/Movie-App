package com.example.khaled.demofragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Khaled on 9/1/2016.
 */
public class DetailsFragment extends Fragment {
    Bundle mBundle;
    Bundle restoredDate;

    String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    String REQUEST_BASE_URL = "http://api.themoviedb.org/3/movie/";
    final String KEY = "9ae53f3101bef7b8b4015690ea82b38c";

    private DatabaseHelper mydb;

    ListView listViewTrailers;
    ListView listViewReviews;
    ImageView imageViewPoster;

    JSONObject jsonObjectForSelectedPoster;
    String jsonObjectForTrailers;
    String jsonObjectForReviews;
    int idMovie;
    String year;
    String rate;
    String originalTitle;
    String overview;

    ArrayList<String> listTrailersKeys;

    Button favouriteButton;

    final static String TAG = "Details";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = this.getArguments();
        mydb = new DatabaseHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        TextView textViewTitle = (TextView) rootView.findViewById(R.id.text_view_title);
        imageViewPoster = (ImageView) rootView.findViewById(R.id.img_poster);
        TextView textViewYear = (TextView) rootView.findViewById(R.id.year);
        TextView textViewRate = (TextView) rootView.findViewById(R.id.rate);
        TextView textViewOverview = (TextView) rootView.findViewById(R.id.text_view_overview);
        favouriteButton = (Button) rootView.findViewById(R.id.add_favourite);
        listViewTrailers = (ListView) rootView.findViewById(R.id.list_view_trailers);
        listViewReviews = (ListView) rootView.findViewById(R.id.list_view_reviews);

        try {
            jsonObjectForSelectedPoster = new JSONObject(mBundle.getString("jsonObjectForSelectedPoster"));
            originalTitle = jsonObjectForSelectedPoster.getString("original_title");
            textViewTitle.setText(originalTitle);
            idMovie = jsonObjectForSelectedPoster.getInt("id");

            if (mBundle.getString("endPoint").equals("favourite") && isInDatabase(idMovie) ) {
                // edit this from database
                String trailersString = mBundle.getString("trailers");
                Log.d(TAG, trailersString);
                String reviewsString = mBundle.getString("reviews");
                if(trailersString != null && reviewsString != null){
                    JSONObject trailersJson = new JSONObject(trailersString);
                    listViewTrailers.setAdapter(new TrailersAdapter(getActivity(), trailersJson));
                    listTrailersKeys = new ArrayList<String>();
                    for(int i = 0; i < trailersJson.getJSONArray("results").length(); i++){
                        Log.d(TAG, trailersJson.getJSONArray("results").getJSONObject(i).getString("key"));
                        listTrailersKeys.add(trailersJson.getJSONArray("results").getJSONObject(i).getString("key"));
                    }

                    JSONObject reviewsJson = new JSONObject(reviewsString);
                    listViewReviews.setAdapter(new ReviewsAdapter(getActivity(), reviewsJson));
                }
                //mBundle.putInt("id",idsListFromDB.get(position));
                //mBundle.putInt("id", cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID_COLUMN)));
                //byte[] poster = mBundle.getByteArray("poster");
                byte[] poster = mydb.getPoster(idMovie);
                Bitmap bitmap = BitmapFactory.decodeByteArray(poster, 0 ,poster.length);
                imageViewPoster.setImageBitmap(bitmap);
                favouriteButton.setVisibility(View.INVISIBLE);
            }
            else {
                if(isInDatabase(idMovie))
                    favouriteButton.setVisibility(View.INVISIBLE);

                Picasso.with(getActivity()).load(IMAGES_BASE_URL + jsonObjectForSelectedPoster.getString("poster_path")).into(imageViewPoster);
                makeRequest("videos");
                makeRequest("reviews");
            }

            year = jsonObjectForSelectedPoster.getString("release_date").substring(0,4);
            textViewYear.setText(year);
            rate = jsonObjectForSelectedPoster.getInt("vote_average") + "/10";
            textViewRate.setText(rate);
            overview = jsonObjectForSelectedPoster.getString("overview");
            textViewOverview.setText(overview);


            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToDatabase();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        listViewTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + listTrailersKeys.get(position))));
            }
        });

        return rootView;
    }

    void makeRequest(final String endPoint) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = REQUEST_BASE_URL + idMovie + "/" + endPoint  + "?api_key=" + KEY;

        // Request a string response from the provided URL.
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(endPoint.equals("videos")) {
                                jsonObjectForTrailers = response.toString();
                                Log.d(TAG, jsonObjectForTrailers.toString());
                                listViewTrailers.setAdapter(new TrailersAdapter(getActivity(), response));
                                listTrailersKeys = new ArrayList<String>();
                                for(int i = 0; i < response.getJSONArray("results").length(); i++){
                                    listTrailersKeys.add(response.getJSONArray("results").getJSONObject(i).getString("key"));
                                }
                            }

                            else if(endPoint.equals("reviews")) {
                                jsonObjectForReviews = response.toString();
                                Log.d(TAG, jsonObjectForReviews.toString());
                                listViewReviews.setAdapter(new ReviewsAdapter(getActivity(), response));
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Volley Error " + endPoint, Toast.LENGTH_LONG).show();
            }
        });
// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    void addToDatabase(){

        Bitmap poster = ((BitmapDrawable)imageViewPoster.getDrawable()).getBitmap();
        byte[] posterByte = getBitmapAsByteArray(poster);

        if(mydb.insertMovie(idMovie, posterByte, jsonObjectForTrailers,jsonObjectForReviews,jsonObjectForSelectedPoster.toString())){
            Toast.makeText(getActivity(),"Inserted to Favourites successfully", Toast.LENGTH_LONG).show();
            favouriteButton.setVisibility(View.INVISIBLE);
        }
        else {
            Toast.makeText(getActivity(),"Can't insert to Favourites", Toast.LENGTH_LONG).show();
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public boolean isInDatabase(int idMovie){
        return mydb.checkMovie(idMovie);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("jsonObjectForSelectedPoster", jsonObjectForSelectedPoster.toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoredDate = savedInstanceState;
    }
}
