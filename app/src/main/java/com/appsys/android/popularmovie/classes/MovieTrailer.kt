package com.appsys.android.popularmovie.classes

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.appsys.android.popularmovie.api.TheMovieDbApi.Companion.getYouTubeImagePath
import com.appsys.android.popularmovie.api.TheMovieDbApi.Companion.getYouTubeVideoPath

/**
 * Created by shakir on 8/8/2017.
 */
class MovieTrailer : Parcelable {
    var movieId: Int
        private set
    private var mKey: String?
    var title: String?
        private set
    private var mSite: String?
    private var mType: String?

    constructor(movieId: Int, key: String?, title: String?, site: String?, type: String?) {
        this.movieId = movieId
        mKey = key
        this.title = title
        mSite = site
        mType = type
    }

    constructor(parcel: Parcel) {
        movieId = parcel.readInt()
        mKey = parcel.readString()
        title = parcel.readString()
        mSite = parcel.readString()
        mType = parcel.readString()
    }

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(movieId)
        parcel.writeString(mKey)
        parcel.writeString(title)
        parcel.writeString(mSite)
        parcel.writeString(mType)
    }

    val imageUrl: Uri
        get() = getYouTubeImagePath(mKey)

    val videoUrl: Uri
        get() = getYouTubeVideoPath(mKey)

    companion object CREATOR : Parcelable.Creator<MovieTrailer> {
        override fun createFromParcel(parcel: Parcel) = MovieTrailer(parcel)

        override fun newArray(i: Int): Array<MovieTrailer?> = arrayOfNulls<MovieTrailer>(i)
    }
}