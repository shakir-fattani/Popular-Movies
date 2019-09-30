package com.appsys.android.popularmovie.api;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsys.android.popularmovie.classes.Movie;
import com.appsys.android.popularmovie.classes.MovieDbException;
import com.appsys.android.popularmovie.classes.MovieDbJSONException;
import com.appsys.android.popularmovie.classes.MovieReview;
import com.appsys.android.popularmovie.classes.MovieTrailer;
import com.appsys.android.popularmovie.classes.SearchResult;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by shakir on 8/10/2017.
 */

public class TheMovieDbApi {
    private static final TheMovieDbApi ourInstance = new TheMovieDbApi();
    private static final String TAG = TheMovieDbApi.class.getSimpleName();

    private String mScheme = "https";
    private final String API_HOST = "api.themoviedb.org";
    private final String IMAGE_HOST = "image.tmdb.org";
    private final String API_VERSION = "3";
    private String mLanguage = "en-us";
    private String mApikey = "";

    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String THUMB_BASE_URL = MOVIE_POSTER_BASE_URL + "/w342";
    public static final String POSTER_BASE_URL = MOVIE_POSTER_BASE_URL + "/w780";

    private TheMovieDbApi() {}

    public static TheMovieDbApi getInstance() {
        return ourInstance;
    }

    public void setApiKey(String apiKey) { mApikey = apiKey; }

    public SearchResult<Movie> getPopularMovies(int pageNo) throws IOException, MovieDbException {
        return getMovies("popular", pageNo);
    }

    public static Uri getYouTubeVideoPath(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", id);
        return builder.build();
    }

    public static Uri getYouTubeImagePath(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("i.ytimg.com")
                .appendPath("vi")
                .appendPath(id)
                .appendPath("mqdefault.jpg");
        return builder.build();
    }

    public SearchResult<Movie> getTopRatedMovies(int pageNo) throws IOException, MovieDbException {
        return getMovies("top_rated", pageNo);
    }

    public SearchResult<Movie> getUpcomingMovies(int pageNo) throws IOException, MovieDbException {
        return getMovies("upcoming", pageNo);
    }

    private SearchResult<Movie> getMovies(String type, int pageNo) throws IOException, MovieDbException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(pageNo));

        Uri uri = getUriPath("movie/" + type, params);

        JSONObject result = getResponse(uri);
        try {
            return new SearchResult<Movie>(result.getInt("total_pages"), result.getInt("total_results"), result.getInt("page"), getMovieArrayByJSON(result));
        } catch (JSONException e) {
            throw new MovieDbJSONException("Api response is not json: " + e.getMessage(), result.toString());
        }
    }

    public SearchResult<MovieReview> getMovieReview(int movieId, int pageNo) throws IOException, MovieDbException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(pageNo));

        Uri uri = getUriPath("movie/" + String.valueOf(movieId) + "/reviews", params);

        JSONObject result = getResponse(uri);
        try {
            return new SearchResult<MovieReview>(result.getInt("total_pages"), result.getInt("total_results"), result.getInt("page"), getMovieReviewArrayByJSON(result));
        } catch (JSONException e) {
            throw new MovieDbJSONException("Api response is not json: " + e.getMessage(), result.toString());
        }
    }

    public ArrayList<MovieTrailer> getMovieTrailer(int movieId) throws IOException, MovieDbException {
        Uri uri = getUriPath("movie/" + String.valueOf(movieId) + "/videos", null);

        JSONObject result = getResponse(uri);
        return getMovieTrailerArrayByJSON(result);
    }

    private ArrayList<Movie> getMovieArrayByJSON(JSONObject json) throws MovieDbException {
        try {
            JSONArray jsonArray = json.getJSONArray("results");

            ArrayList<Movie> m = new ArrayList<Movie>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieJson = jsonArray.getJSONObject(i);
                Movie tempM = new Movie(movieJson.getInt("id"),
                        movieJson.getString("title"),
                        movieJson.getString("poster_path"),
                        movieJson.getString("backdrop_path"),
                        movieJson.getString("overview"),
                        movieJson.getString("vote_average"),
                        movieJson.getString("original_language"),
                        movieJson.getString("release_date"));
                m.add(tempM);
            }
            return m;
        } catch (JSONException e) {
            throw new MovieDbJSONException("Api response is not json: " + e.getMessage(), json.toString());
        }
    }

    private ArrayList<MovieReview> getMovieReviewArrayByJSON(JSONObject json) throws MovieDbException {
        try {
            JSONArray jsonArray = json.getJSONArray("results");

            ArrayList<MovieReview> m = new ArrayList<MovieReview>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieJson = jsonArray.getJSONObject(i);
                MovieReview tempM = new MovieReview(movieJson.getString("author"), movieJson.getString("content"));
                m.add(tempM);
            }
            return m;
        } catch (JSONException e) {
            throw new MovieDbJSONException("Api response is not json: " + e.getMessage(), json.toString());
        }
    }

    private ArrayList<MovieTrailer> getMovieTrailerArrayByJSON(JSONObject json) throws MovieDbException {
        try {
            JSONArray jsonArray = json.getJSONArray("results");

            ArrayList<MovieTrailer> m = new ArrayList<MovieTrailer>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieJson = jsonArray.getJSONObject(i);
                MovieTrailer tempM = new MovieTrailer(json.getInt("id"), movieJson.getString("key"), movieJson.getString("name"), movieJson.getString("site"), movieJson.getString("type"));
                m.add(tempM);
            }
            return m;
        } catch (JSONException e) {
            throw new MovieDbJSONException("Api response is not json: " + e.getMessage(), json.toString());
        }
    }

    private Uri getUriPath(String path, Map<String, String> params) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(mScheme)
                .authority(API_HOST)
                .appendPath(API_VERSION);

        if (path != null) {
            String[] p = path.split("/");
            for (String temp : p)
                builder.appendPath(temp);
        }

        builder.appendQueryParameter("api_key", mApikey)
                .appendQueryParameter("language", mLanguage);

        if (params != null)
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

        return builder.build();
    }

    private JSONObject getResponse(Uri uri) throws IOException, MovieDbException {
        HttpURLConnection urlConnection = null;
        String response = "";
        try {
            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in;
            boolean success = (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST);
            if (success)
                in = urlConnection.getInputStream();
            else
                in = urlConnection.getErrorStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (!hasInput)
                throw new MovieDbException("Api response is empty");

            response = scanner.next();
            JSONObject json = new JSONObject(response);

            if (success)
                return json;

            throw new MovieDbException(json.getString("status_message"));

        } catch (JSONException e) {
            throw new MovieDbJSONException("Api response is not json: " + e.getMessage(), response);
        } catch (MalformedURLException e) {
            throw new MovieDbException("Uri: " + uri.toString() + ", could not be parsed");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
