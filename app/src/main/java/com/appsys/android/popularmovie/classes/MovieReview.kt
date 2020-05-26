package com.appsys.android.popularmovie.classes

import android.os.Parcel
import android.os.Parcelable
import com.appsys.android.popularmovie.classes.MovieReview
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by shakir on 8/8/2017.
 */
class MovieReview(val author: String?, val message: String?) : Parcelable {
    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(author)
        parcel.writeString(message)
    }

    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString())

    companion object {
        private val TAG = MovieReview::class.java.simpleName
        val CREATOR: Parcelable.Creator<MovieReview> = object : Parcelable.Creator<MovieReview> {
            override fun createFromParcel(parcel: Parcel) = MovieReview(parcel)
            override fun newArray(i: Int) = arrayOfNulls<MovieReview>(i)
        }

        @Throws(JSONException::class)
        fun getByJSON(movieId: Int, movieJson: JSONObject): MovieReview? {
            var m: MovieReview? = null
            try {
                m = MovieReview(movieJson.getString("author"), movieJson.getString("content"))
            } catch (e: JSONException) {
            }
            return m
        }

        @Throws(JSONException::class)
        fun getArrayByJSON(movieId: Int, jsonArray: JSONArray): ArrayList<MovieReview> {
            val m = ArrayList<MovieReview>()
            for (i in 0 until jsonArray.length()) {
                val jo = jsonArray.getJSONObject(i)
                val tempM = getByJSON(movieId, jo)
                if (tempM != null) m.add(tempM)
            }
            return m
        }
    }
}