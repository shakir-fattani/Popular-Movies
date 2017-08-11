package com.appsys.android.popularmovie.classes;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.appsys.android.popularmovie.api.TheMovieDbApi;

/**
 * Created by shakir on 8/8/2017.
 */

public class MovieTrailer implements Parcelable {

    private static final String TAG = MovieTrailer.class.getSimpleName();

    private int mMovieId;
    private String mKey;
    private String mTitle;
    private String mSite;
    private String mType;

    public MovieTrailer(int movieId, String key, String title, String site, String type) {
        mMovieId = movieId;
        mKey = key;
        mTitle = title;
        mSite = site;
        mType = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mMovieId);
        parcel.writeString(mKey);
        parcel.writeString(mTitle);
        parcel.writeString(mSite);
        parcel.writeString(mType);
    }

    public final static Parcelable.Creator<MovieTrailer> CREATOR = new Parcelable.Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel parcel) {
            return new MovieTrailer(parcel);
        }

        @Override
        public MovieTrailer[] newArray(int i) {
            return new MovieTrailer[0];
        }
    };

    public MovieTrailer(Parcel parcel) {
        mMovieId = parcel.readInt();
        mKey = parcel.readString();
        mTitle = parcel.readString();
        mSite = parcel.readString();
        mType = parcel.readString();
    }

    public int getMovieId() {
        return mMovieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Uri getImageUrl() {
        return TheMovieDbApi.getYouTubeImagePath(mKey);
    }

    public Uri getVideoUrl() {
        return TheMovieDbApi.getYouTubeVideoPath(mKey);
    }
}
