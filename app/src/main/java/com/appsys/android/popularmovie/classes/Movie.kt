package com.appsys.android.popularmovie.classes

import android.os.Parcel
import android.os.Parcelable
import com.appsys.android.popularmovie.api.TheMovieDbApi

/**
 * Created by shakir on 8/2/2017.
 */
class Movie(val id: Int, val title: String, val posterPath: String, val backdropPath: String, val overview: String, val rating: String, val language: String, val release: String) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString()
            ?: "", parcel.readString() ?: "", parcel.readString() ?: "", parcel.readString()
            ?: "", parcel.readString() ?: "", parcel.readString() ?: "", parcel.readString() ?: "")

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(posterPath)
        parcel.writeString(backdropPath)
        parcel.writeString(overview)
        parcel.writeString(rating)
        parcel.writeString(language)
        parcel.writeString(release)
    }

    val poster: String
        get() = TheMovieDbApi.THUMB_BASE_URL + posterPath

    val backdrop: String
        get() = TheMovieDbApi.POSTER_BASE_URL + backdropPath


    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel) = Movie(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Movie>(size)
    }
}