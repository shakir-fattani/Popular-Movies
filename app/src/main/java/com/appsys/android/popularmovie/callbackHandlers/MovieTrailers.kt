package com.appsys.android.popularmovie.callbackHandlers

import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import com.appsys.android.popularmovie.MovieDetail
import com.appsys.android.popularmovie.api.TheMovieDbApi
import com.appsys.android.popularmovie.classes.MovieDbException
import com.appsys.android.popularmovie.classes.MovieTrailer
import java.io.IOException
import java.util.*

/**
 * Created by shakir on 8/8/2017.
 */
class MovieTrailers(private val mMovieId: Int, private val mMovieDetail: MovieDetail) : LoaderManager.LoaderCallbacks<ArrayList<MovieTrailer>?> {
    var errorMessage: String? = ""
        private set

    private fun loadMovies() {
        val b = Bundle().apply {
            putInt("MovieId", mMovieId)
        }
        val movieApi = LoaderManager.getInstance(mMovieDetail).getLoader<ArrayList<MovieTrailer>>(AsyncLoaderMovie_TRAILER)
        if (movieApi == null) {
            LoaderManager.getInstance(mMovieDetail).initLoader(AsyncLoaderMovie_TRAILER, b, this)
        } else {
            LoaderManager.getInstance(mMovieDetail).restartLoader(AsyncLoaderMovie_TRAILER, b, this)
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<ArrayList<MovieTrailer>?> {
        return object : AsyncTaskLoader<ArrayList<MovieTrailer>?>(mMovieDetail) {
            override fun onStartLoading() {
                super.onStartLoading()
                if (bundle == null) return
                forceLoad()
            }

            override fun loadInBackground(): ArrayList<MovieTrailer>? {
                try {
                    return TheMovieDbApi.instance.getMovieTrailer(bundle?.getInt("MovieId") ?: 1)
                } catch (e: MovieDbException) {
                    errorMessage = e.message
                    e.printStackTrace()
                } catch (e: IOException) {
                    errorMessage = "Please check your Internet connection"
                    e.printStackTrace()
                } catch (e: Exception) {
                    errorMessage = "Main: " + e.message
                    e.printStackTrace()
                }
                return null
            }
        }
    }

    override fun onLoadFinished(loader: Loader<ArrayList<MovieTrailer>?>, movies: ArrayList<MovieTrailer>?) {
        if (movies != null) {
            mMovieDetail.trailerAdapter!!.setMoviesData(movies)
            if (movies.size < 1) mMovieDetail.showMessage("Trailer list is empty yet")
        } else {
            mMovieDetail.showMessage(errorMessage)
        }
        LoaderManager.getInstance(mMovieDetail).destroyLoader(AsyncLoaderMovie_TRAILER)
    }

    override fun onLoaderReset(loader: Loader<ArrayList<MovieTrailer>?>) {}

    companion object {
        const val AsyncLoaderMovie_TRAILER = 26
    }

    init {
        LoaderManager.getInstance(mMovieDetail).initLoader(mMovieId, null, this)
        loadMovies()
    }
}