package com.appsys.android.popularmovie.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import com.appsys.android.popularmovie.data.MovieListContract.MovieListEntry

/**
 * Created by shakir on 8/9/2017.
 */
class MovieContentProvider : ContentProvider() {
    private var mMovieListHelper: MovieListHelper? = null
    override fun onCreate(): Boolean {
        mMovieListHelper = MovieListHelper(context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mMovieListHelper!!.writableDatabase
        val returnUri = when (sUriMatcher.match(uri)) {
            TASKS -> {
                val id = db.insert(MovieListEntry.TABLE_NAME, null, values)
                if (id > 0) {
                    ContentUris.withAppendedId(MovieListEntry.CONTENT_URI, id)
                } else {
                    throw SQLException("Failed to insert row into $uri")
                }
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = mMovieListHelper!!.readableDatabase
        val retCursor = when (sUriMatcher.match(uri)) {
            TASKS -> db.query(MovieListEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        retCursor.setNotificationUri(context!!.contentResolver, uri)
        return retCursor
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mMovieListHelper!!.writableDatabase
        val tasksDeleted = when (sUriMatcher.match(uri)) {
            TASK_WITH_ID -> {
                val id = uri.pathSegments[1]
                db.delete(MovieListEntry.TABLE_NAME, MovieListEntry.COLUMN_ID + "=?", arrayOf(id))
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (tasksDeleted != 0)
            context!!.contentResolver.notifyChange(uri, null)
        return tasksDeleted
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    companion object {
        const val TASKS = 100
        const val TASK_WITH_ID = 101
        private val sUriMatcher = buildUriMatcher()
        fun buildUriMatcher() = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(MovieListContract.AUTHORITY, MovieListContract.PATH_TASKS, TASKS)
            addURI(MovieListContract.AUTHORITY, MovieListContract.PATH_TASKS + "/#", TASK_WITH_ID)
        }
    }
}