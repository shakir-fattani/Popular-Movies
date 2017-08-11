package com.appsys.android.popularmovie.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shakir on 8/8/2017.
 */

public class MovieReview implements Parcelable {

    private static final String TAG = MovieReview.class.getSimpleName();
    private String mAuthor;
    private String mMessage;

    public MovieReview(String author, String message){
        mAuthor = author;
        mMessage = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuthor);
        parcel.writeString(mMessage);
    }

    public final static Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel parcel) {
            return new MovieReview(parcel);
        }

        @Override
        public MovieReview[] newArray(int i) {
            return new MovieReview[0];
        }
    };

    public MovieReview(Parcel parcel) {
        mAuthor = parcel.readString();
        mMessage = parcel.readString();
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getMessage() {
        return mMessage;
    }

    public static MovieReview getByJSON(int movieId, JSONObject movieJson) throws JSONException {
        MovieReview m = null;
        try {
            m = new MovieReview(movieJson.getString("author"), movieJson.getString("content"));
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return m;
    }

    public static ArrayList<MovieReview> getArrayByJSON(int movieId, JSONArray jsonArray) throws JSONException {
        ArrayList<MovieReview> m = new ArrayList<MovieReview>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            MovieReview tempM = getByJSON(movieId, jo);
            if (tempM != null)
                m.add(tempM);
        }
        return m;
    }
}