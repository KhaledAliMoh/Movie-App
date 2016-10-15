package com.example.khaled.demofragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Khaled on 9/1/2016.
 */
public class PosterFragment extends Fragment {
    String BASE_URL = "http://api.themoviedb.org/3/movie/";
    final String KEY = ""; // Write your KEY here.

    String endPoint = "popular";         // default end point with popular
    ArrayList<String> listOfPostersPaths;
    GridView gridview;
    ImageAdapter imageAdapter;
    JSONArray data;

    DatabaseHelper mydb;
    ArrayList<Integer> idsListFromDB;
    Bundle mBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mydb = new DatabaseHelper(getActivity());
        mBundle = new Bundle();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);
        gridview = (GridView) rootView.findViewById(R.id.list);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Bundle mBundle = new Bundle();
                try{
                    if(endPoint.equals("favourite")) {
                        Cursor cursor = mydb.getAllMovieDetails(idsListFromDB.get(position));
                        mBundle.putString("jsonObjectForSelectedPoster", cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDITIONAL_DATA_COLUMN)));
                        mBundle.putString("trailers", cursor.getString(cursor.getColumnIndex(DatabaseHelper.TRAILERS_OBJECT_COLUMN)));
                        mBundle.putString("reviews", cursor.getString(cursor.getColumnIndex(DatabaseHelper.REVIEWS_OBJECT_COLUMN)));
                        //mBundle.putInt("id",idsListFromDB.get(position));
                        mBundle.putInt("id", cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID_COLUMN)));
                        mBundle.putByteArray("poster", cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.POSTER_COLUMN)));
                    }
                    else
                        mBundle.putString("jsonObjectForSelectedPoster", data.getJSONObject(position).toString());

                }catch (Exception e) {
                    e.getMessage();
                }

                mBundle.putString("endPoint", endPoint);
                Fragment detailsFragment = new DetailsFragment();
                detailsFragment.setArguments(mBundle);
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || getResources().getConfiguration().orientation == Configuration.SCREENLAYOUT_SIZE_LARGE)
                    transaction.replace(R.id.container2, detailsFragment);

                else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT || getResources().getConfiguration().orientation == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
                    transaction.add(R.id.container, detailsFragment);
                    transaction.addToBackStack(null);
                }
                transaction.commit();
            }
        });
        if(isOnline()){
            makeRequest();
        }
        else {
            showAllFavourites();
        }
        return rootView;
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.popular_item && isOnline()) {
            endPoint = "popular";
            makeRequest();
        }

        else if (item.getItemId() == R.id.top_rated_item && isOnline()) {
            endPoint = "top_rated";
            makeRequest();
        }

        else if(item.getItemId() == R.id.favourite_item){
            endPoint = "favourite";
            showAllFavourites();
        }

        else {
                // when the user select popular or top rate and there is no internet connection
            Toast.makeText(getActivity(), "There is no Internet connection ", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    void makeRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = BASE_URL + endPoint + "?api_key=" + KEY;

            // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            listOfPostersPaths = new ArrayList<String>();
                            data = response.getJSONArray("results");
                            for (int i = 0; i < data.length(); i++) {
                                listOfPostersPaths.add(data.getJSONObject(i).getString("poster_path"));
                            }
                            imageAdapter = new ImageAdapter(getActivity(), listOfPostersPaths);
                            gridview.setAdapter(imageAdapter);
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    void showAllFavourites(){
        endPoint = "favourite";
        // get data from database then fill data array list with
        idsListFromDB = mydb.getIDsOfAllFavourites();
        if (idsListFromDB != null)
            gridview.setAdapter(new ImageAdapterDB(getActivity(),idsListFromDB));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("endPoint",endPoint);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBundle = savedInstanceState;
    }
}
