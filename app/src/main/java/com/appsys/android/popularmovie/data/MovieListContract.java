package com.appsys.android.popularmovie.data;

/**
 * Created by shakir on 8/9/2017.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieListContract {
    public static final String AUTHORITY = "com.appsys.android.popularmovie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "tasks";

    public static final class MovieListEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "movielist";
        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_RELEASE = "release";
    }
}
