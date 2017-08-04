package com.appsys.android.popularmovie;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appsys.android.popularmovie.classes.MovieDbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by shakir on 8/2/2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String LANGUAGE = "en-us";
    private static final String API_KEY = "[YOUR_API_KEY]";
    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    public static final String THUMB_BASE_URL = MOVIE_POSTER_BASE_URL + "/w342";
    public static final String POSTER_BASE_URL = MOVIE_POSTER_BASE_URL + "/w780";

    private static String getPath(String path, int page) {
        return "movie/" + path + "?api_key=" + API_KEY + "&language=" + LANGUAGE + "&page=" + String.valueOf(page);
    }

    public static JSONObject getPopularMovies(int page) throws IOException, MovieDbException {
        return getMovies("popular", page);
    }

    public static JSONObject getTopRatedMovies(int page) throws IOException, MovieDbException {
        return getMovies("top_rated", page);
    }

    public static JSONObject getMovies(String path, int page) throws IOException, MovieDbException {
        String str = getPath(path, page);
        URL url = getUrl(str);
        if (url != null) {
            String response = getResult(url);
            if (response != null && !response.equals("")) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("success") && !json.getBoolean("success")){
                        if (json.has("status_message"))
                            throw new MovieDbException(json.getString("status_message"));

                        throw new MovieDbException("Api Error");
                    }
                    return json;
                } catch (JSONException e) {
                    throw new MovieDbException("Api response is not compatible with the app");
                }
            }
        }

        throw new MovieDbException("Api address could not found");
    }

    private static URL getUrl(String query) {
        URL  url = null;
        Uri uri = Uri.parse(MOVIE_BASE_URL + query);

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.d(TAG, e.getMessage());
        }
        return url;
    }


    @Nullable
    private static String getResult(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }

    }

}
