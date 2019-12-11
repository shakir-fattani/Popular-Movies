package com.appsys.android.popularmovie.api

import android.net.Uri
import com.appsys.android.popularmovie.classes.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * Created by shakir on 8/10/2017.
 */
class TheMovieDbApi private constructor() {
    private val API_HOST = "api.themoviedb.org"
    private val IMAGE_HOST = "image.tmdb.org"
    private val API_VERSION = "3"
    private val scheme = "https"
    private val language = "en-us"
    var apikey = "479407e3eb2f80c4ee8f711ffaa9cb63"

    @Throws(IOException::class, MovieDbException::class)
    fun getPopularMovies(pageNo: Int) = getMovies("popular", pageNo)

    @Throws(IOException::class, MovieDbException::class)
    fun getTopRatedMovies(pageNo: Int) = getMovies("top_rated", pageNo)

    @Throws(IOException::class, MovieDbException::class)
    fun getUpcomingMovies(pageNo: Int) = getMovies("upcoming", pageNo)

    @Throws(IOException::class, MovieDbException::class)
    private fun getMovies(type: String, pageNo: Int): SearchResult<Movie> {
        val params = HashMap<String, String>()
        params["page"] = pageNo.toString()
        val uri = getUriPath("movie/$type", params)
        val result = getResponse(uri)
        return try {
            SearchResult(result.getInt("total_pages"), result.getInt("total_results"), result.getInt("page"), getMovieArrayByJSON(result))
        } catch (e: JSONException) {
            throw MovieDbJSONException("Api response is not json: " + e.message, result.toString())
        }
    }

    @Throws(IOException::class, MovieDbException::class)
    fun getMovieReview(movieId: Int, pageNo: Int): SearchResult<MovieReview> {
        val params = HashMap<String, String>()
        params["page"] = pageNo.toString()
        val uri = getUriPath("movie/$movieId/reviews", params)
        val result = getResponse(uri)
        return try {
            SearchResult(result.getInt("total_pages"), result.getInt("total_results"), result.getInt("page"), getMovieReviewArrayByJSON(result))
        } catch (e: JSONException) {
            throw MovieDbJSONException("Api response is not json: " + e.message, result.toString())
        }
    }

    @Throws(IOException::class, MovieDbException::class)
    fun getMovieTrailer(movieId: Int): ArrayList<MovieTrailer> {
        val uri = getUriPath("movie/$movieId/videos", null)
        val result = getResponse(uri)
        return getMovieTrailerArrayByJSON(result)
    }

    @Throws(MovieDbException::class)
    private fun getMovieArrayByJSON(json: JSONObject): ArrayList<Movie> {
        return try {
            val jsonArray = json.getJSONArray("results")
            val m = ArrayList<Movie>()
            for (i in 0 until jsonArray.length()) {
                val mj = jsonArray.getJSONObject(i)
                m.add(Movie(mj.getInt("id"),
                        mj.getString("title"),
                        mj.getString("poster_path"),
                        mj.getString("backdrop_path"),
                        mj.getString("overview"),
                        mj.getString("vote_average"),
                        mj.getString("original_language"),
                        mj.getString("release_date")))
            }
            m
        } catch (e: JSONException) {
            throw MovieDbJSONException("Api response is not json: " + e.message, json.toString())
        }
    }

    @Throws(MovieDbException::class)
    private fun getMovieReviewArrayByJSON(json: JSONObject): ArrayList<MovieReview> {
        return try {
            val jsonArray = json.getJSONArray("results")
            val m = ArrayList<MovieReview>()
            for (i in 0 until jsonArray.length()) {
                val mj = jsonArray.getJSONObject(i)
                m.add(MovieReview(mj.getString("author"), mj.getString("content")))
            }
            m
        } catch (e: JSONException) {
            throw MovieDbJSONException("Api response is not json: " + e.message, json.toString())
        }
    }

    @Throws(MovieDbException::class)
    private fun getMovieTrailerArrayByJSON(json: JSONObject): ArrayList<MovieTrailer> {
        return try {
            val jsonArray = json.getJSONArray("results")
            val m = ArrayList<MovieTrailer>()
            for (i in 0 until jsonArray.length()) {
                val mj = jsonArray.getJSONObject(i)
                m.add(MovieTrailer(json.getInt("id"), mj.getString("key"), mj.getString("name"), mj.getString("site"), mj.getString("type")))
            }
            m
        } catch (e: JSONException) {
            throw MovieDbJSONException("Api response is not json: " + e.message, json.toString())
        }
    }

    private fun getUriPath(path: String?, params: Map<String, String>?) = Uri.Builder().apply {
        scheme(scheme)
        authority(API_HOST)
        appendPath(API_VERSION)
        if (path != null) {
            val p = path.split("/").toTypedArray()
            for (temp in p) appendPath(temp)
        }
        appendQueryParameter("api_key", apikey)
                .appendQueryParameter("language", language)
        if (params != null) for ((key, value) in params) {
            appendQueryParameter(key, value)
        }
    }.build()

    @Throws(IOException::class, MovieDbException::class)
    private fun getResponse(uri: Uri): JSONObject {
        var urlConnection: HttpURLConnection? = null
        var response = ""
        try {
            val url = URL(uri.toString())
            urlConnection = url.openConnection() as HttpURLConnection
            val `in`: InputStream
            val success = urlConnection.responseCode < HttpURLConnection.HTTP_BAD_REQUEST
            `in` = if (success) urlConnection.inputStream else urlConnection.errorStream
            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (!hasInput) throw MovieDbException("Api response is empty")
            response = scanner.next()
            val json = JSONObject(response)
            if (success) return json
            throw MovieDbException(json.getString("status_message"))
        } catch (e: JSONException) {
            throw MovieDbJSONException("Api response is not json: " + e.message, response)
        } catch (e: MalformedURLException) {
            throw MovieDbException("Uri: $uri, could not be parsed")
        } finally {
            urlConnection?.disconnect()
        }
    }

    companion object {
        val instance = TheMovieDbApi()
        private val TAG = TheMovieDbApi::class.java.simpleName
        private const val MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/"
        const val THUMB_BASE_URL = "$MOVIE_POSTER_BASE_URL/w342"
        const val POSTER_BASE_URL = "$MOVIE_POSTER_BASE_URL/w780"

        @JvmStatic
        fun getYouTubeVideoPath(id: String?) = Uri.Builder().apply {
            scheme("https")
            authority("www.youtube.com")
            appendPath("watch")
            appendQueryParameter("v", id)
        }.build()

        @JvmStatic
        fun getYouTubeImagePath(id: String?) = Uri.Builder().apply {
            scheme("https")
            authority("i.ytimg.com")
            appendPath("vi")
            appendPath(id)
            appendPath("mqdefault.jpg")
        }.build()
    }
}