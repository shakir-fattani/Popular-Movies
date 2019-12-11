package com.appsys.android.popularmovie.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by shakir on 8/9/2017.
 */
class MovieListHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) =
            sqLiteDatabase.execSQL("CREATE TABLE " + MovieListContract.MovieListEntry.TABLE_NAME + " (" +
                    MovieListContract.MovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MovieListContract.MovieListEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                    MovieListContract.MovieListEntry.COLUMN_TITLE + " VARCHAR NOT NULL, " +
                    MovieListContract.MovieListEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                    MovieListContract.MovieListEntry.COLUMN_BACKDROP + " VARCHAR NOT NULL, " +
                    MovieListContract.MovieListEntry.COLUMN_POSTER + " VARCHAR NOT NULL, " +
                    MovieListContract.MovieListEntry.COLUMN_RATING + " VARCHAR NOT NULL, " +
                    MovieListContract.MovieListEntry.COLUMN_LANGUAGE + " VARCHAR NOT NULL, " +
                    MovieListContract.MovieListEntry.COLUMN_RELEASE + " VARCHAR NOT NULL " +
                    "); ")

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListContract.MovieListEntry.TABLE_NAME + " ;")
        onCreate(sqLiteDatabase)
    }

    companion object {
        private const val DATABASE_NAME = "waitlist.db"
        private const val DATABASE_VERSION = 2
    }
}