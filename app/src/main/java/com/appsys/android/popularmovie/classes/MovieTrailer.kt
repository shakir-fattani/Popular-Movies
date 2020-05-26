package com.appsys.android.popularmovie.classes

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.appsys.android.popularmovie.api.TheMovieDbApi.Companion.getYouTubeImagePath
import com.appsys.android.popularmovie.api.TheMovieDbApi.Companion.getYouTubeVideoPath

/**
 * Created by shakir on 8/8/2017.
 */
class MovieTrailer(val movieId: Int, val key: String?, val title: String?, val site: String?, val type: String?) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString())

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(movieId)
        parcel.writeString(key)
        parcel.writeString(title)
        parcel.writeString(site)
        parcel.writeString(type)
    }

    val imageUrl: Uri
        get() = getYouTubeImagePath(key)

    val videoUrl: Uri
        get() = getYouTubeVideoPath(key)

    companion object CREATOR : Parcelable.Creator<MovieTrailer> {
        override fun createFromParcel(parcel: Parcel) = MovieTrailer(parcel)

        override fun newArray(i: Int) = arrayOfNulls<MovieTrailer>(i)
    }
}