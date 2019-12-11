package com.appsys.android.popularmovie.data

import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by shakir on 8/9/2017.
 */
object MovieListContract {
    const val AUTHORITY = "com.appsys.android.popularmovie"
    val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")
    const val PATH_TASKS = "tasks"

    object MovieListEntry : BaseColumns{
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build()
        const val _COUNT = "_count"
        const val _ID = "_id"
        const val TABLE_NAME = "movielist"
        const val COLUMN_ID = "movieId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_POSTER = "poster"
        const val COLUMN_BACKDROP = "backdrop"
        const val COLUMN_OVERVIEW = "overview"
        const val COLUMN_RATING = "rating"
        const val COLUMN_LANGUAGE = "language"
        const val COLUMN_RELEASE = "release"
    }
}