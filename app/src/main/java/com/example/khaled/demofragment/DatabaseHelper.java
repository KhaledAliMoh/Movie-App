package com.example.khaled.demofragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Khaled on 9/4/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MovieAppDB.db";
    public static final String TABLE_NAME = "movie";
    public static final String ID_COLUMN = "id";
    public static final String POSTER_COLUMN = "poster";
    public static final String TRAILERS_OBJECT_COLUMN = "trailers";
    public static final String REVIEWS_OBJECT_COLUMN = "reviews";
    public static final String ADDITIONAL_DATA_COLUMN = "information";
//    public static final String TITLE_COLUMN = "title";
//    public static final String RATE_COLUMN = "rate";
//    public static final String YEAR_COLUMN = "year";
//    public static final String OVERVIEW_COLUMN = "overview";

    final static String TAG = "Database";

    DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the table here
        Log.d(TAG, "Before creation");
//        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " + ID_COLUMN + " INTEGER PRIMARY KEY , "
//                + POSTER_COLUMN + " BLOB, " + TRAILERS_OBJECT_COLUMN + "TEXT, " + REVIEWS_OBJECT_COLUMN + "TEXT, " +
//                TITLE_COLUMN + "TEXT, " + YEAR_COLUMN + "TEXT, " + RATE_COLUMN + "TEXT, " +
//                OVERVIEW_COLUMN + "TEXT );");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " + ID_COLUMN + " INTEGER PRIMARY KEY , "
                + POSTER_COLUMN + " BLOB, " + TRAILERS_OBJECT_COLUMN + " TEXT, " + REVIEWS_OBJECT_COLUMN + " TEXT, " +
                ADDITIONAL_DATA_COLUMN + " TEXT );");
        Log.d(TAG, "After creation");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean insertMovie  (int id, byte[] poster, @Nullable String trailersObject, @Nullable String reviewsObject,
                                 String metadata) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID_COLUMN, id);
        contentValues.put(POSTER_COLUMN, poster);
        contentValues.put(TRAILERS_OBJECT_COLUMN, trailersObject);
        contentValues.put(REVIEWS_OBJECT_COLUMN, reviewsObject);
        contentValues.put(ADDITIONAL_DATA_COLUMN, metadata);

        Log.d(TAG, Integer.toString(id));

        if(db.insert(TABLE_NAME, null, contentValues)== -1)
            return false;

        return true;
    }

    public Integer deleteContact (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public byte[] getPoster(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ID_COLUMN + " = ?";
        String[] selectionArg = {Integer.toString(id)};
        String[] projection = { POSTER_COLUMN };
        Cursor cursor = db.query(TABLE_NAME,projection, selection, selectionArg, null, null,null );
        cursor.moveToFirst();

        byte[] poster = cursor.getBlob(cursor.getColumnIndex(POSTER_COLUMN));

        return poster;
    }

    public ArrayList<Integer> getIDsOfAllFavourites(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = { ID_COLUMN };
        String sortOrder = ID_COLUMN + " DESC";
        Log.d(TAG, "Before AA");
        Cursor cursor = db.query(TABLE_NAME,projection, null, null, null, null,sortOrder );
        Log.d(TAG, cursor.getCount() + " ids");
        cursor.moveToFirst();
        Log.d(TAG, Integer.toString(cursor.getCount()));
        int index = cursor.getColumnIndex(ID_COLUMN);
        for (int i = 0; i < cursor.getCount(); i++) {
            list.add(cursor.getInt(index));
            cursor.moveToNext();
        }
       return list;
    }

    public Cursor getAllMovieDetails(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {ID_COLUMN, POSTER_COLUMN, TRAILERS_OBJECT_COLUMN, REVIEWS_OBJECT_COLUMN, ADDITIONAL_DATA_COLUMN};
        String selection = ID_COLUMN + " = ?";
        String[] selectionArg = {Integer.toString(id)};
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArg, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean checkMovie(int idMovie)throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {ID_COLUMN};
        String selection = ID_COLUMN + " = ?";
        String[] selectionArg = {Integer.toString(idMovie)};
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArg, null, null, null);
        //cursor.moveToFirst();
        if(cursor.getCount() > 0)
            return true;
        else
            return false;
    }
}
