package com.appsys.android.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shakir on 8/9/2017.
 */

public class MovieListHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "waitlist.db";
    private static final int DATABASE_VERSION = 2;

    public MovieListHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + MovieListContract.MovieListEntry.TABLE_NAME + " (" +
            MovieListContract.MovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MovieListContract.MovieListEntry.COLUMN_ID + " INTEGER NOT NULL, " +
            MovieListContract.MovieListEntry.COLUMN_TITLE + " VARCHAR NOT NULL, " +
            MovieListContract.MovieListEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            MovieListContract.MovieListEntry.COLUMN_BACKDROP + " VARCHAR NOT NULL, " +
            MovieListContract.MovieListEntry.COLUMN_POSTER + " VARCHAR NOT NULL, " +
            MovieListContract.MovieListEntry.COLUMN_RATING + " VARCHAR NOT NULL, " +
            MovieListContract.MovieListEntry.COLUMN_LANGUAGE + " VARCHAR NOT NULL, " +
            MovieListContract.MovieListEntry.COLUMN_RELEASE + " VARCHAR NOT NULL " +
            "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListContract.MovieListEntry.TABLE_NAME + " ;");
        onCreate(sqLiteDatabase);
    }
}
