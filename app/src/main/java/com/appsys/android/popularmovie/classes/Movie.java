package com.appsys.android.popularmovie.classes;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.appsys.android.popularmovie.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shakir on 8/2/2017.
 */

public class Movie implements Parcelable {

    private static final String TAG = Movie.class.getSimpleName();

    private int mId;
    private String mTitle;
    private String mPoster;
    private String mBackdrop;
    private String mOverview;
    private String mRating;
    private String mLanguage;
    private String mRelease;

    public Movie(int id, String title, String poster, String backdrop, String overview, String rating, String language, String release) {
        mId = id;
        mTitle = title;
        mPoster = poster;
        mBackdrop = backdrop;
        mOverview = overview;
        mRating = rating;
        mLanguage = language;
        mRelease = release;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mBackdrop);
        parcel.writeString(mOverview);
        parcel.writeString(mRating);
        parcel.writeString(mLanguage);
        parcel.writeString(mRelease);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[0];
        }
    };

    public Movie(Parcel parcel) {
        mId = parcel.readInt();
        mTitle = parcel.readString();
        mPoster = parcel.readString();
        mBackdrop = parcel.readString();
        mOverview = parcel.readString();
        mRating = parcel.readString();
        mLanguage = parcel.readString();
        mRelease = parcel.readString();
    }

    public int getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getPoster() { return NetworkUtils.THUMB_BASE_URL + mPoster; }
    public String getBackdrop() { return NetworkUtils.THUMB_BASE_URL + mBackdrop; }
    public String getOverview() {
        return mOverview;
    }
    public String getRating() {
        return mRating;
    }
    public String getLanguage() {
        return mLanguage;
    }
    public String getRelease() {
        return mRelease;
    }

    public static Movie getByJSON(JSONObject movieJson) throws JSONException {
        Movie m = null;
        try {
            m = new Movie(movieJson.getInt("id"), movieJson.getString("title"), movieJson.getString("poster_path"), movieJson.getString("backdrop_path"), movieJson.getString("overview"), movieJson.getString("vote_average"), movieJson.getString("original_language"), movieJson.getString("release_date"));
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return m;
    }

    public static ArrayList<Movie> getArrayByJSON(JSONArray jsonArray) throws JSONException {
        ArrayList<Movie> m = new ArrayList<Movie>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            Movie tempM = getByJSON(jo);
            if (tempM != null)
                m.add(tempM);
        }
        return m;
    }

}