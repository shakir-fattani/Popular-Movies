package com.appsys.android.popularmovie.callbackHandlers

import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import com.appsys.android.popularmovie.MovieDetail
import com.appsys.android.popularmovie.api.TheMovieDbApi
import com.appsys.android.popularmovie.classes.MovieDbException
import com.appsys.android.popularmovie.classes.MovieReview
import java.io.IOException
import java.util.*

/**
 * Created by shakir on 8/8/2017.
 */
class MovieReviews(private val mMovieId: Int, private val mMovieDetail: MovieDetail) : LoaderManager.LoaderCallbacks<ArrayList<MovieReview>?> {
    var errorMessage: String? = ""
        private set

    private fun loadMovies(pageNo: Int) {
        val b = Bundle().apply {
            putInt("MovieId", mMovieId)
            putInt("Page", pageNo)
        }
        if (LoaderManager.getInstance(mMovieDetail).getLoader<Any>(AsyncLoaderMovie_REVIEWS) == null) {
            LoaderManager.getInstance(mMovieDetail).initLoader(AsyncLoaderMovie_REVIEWS, b, this)
        } else {
            LoaderManager.getInstance(mMovieDetail).restartLoader(AsyncLoaderMovie_REVIEWS, b, this)
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<ArrayList<MovieReview>?> {
        return object : AsyncTaskLoader<ArrayList<MovieReview>?>(mMovieDetail) {
            override fun onStartLoading() {
                super.onStartLoading()
                if (bundle == null) return
                forceLoad()
            }

            override fun loadInBackground(): ArrayList<MovieReview>? {
                val page = bundle?.getInt("Page") ?: 0
                val id = bundle?.getInt("MovieId") ?: 0
                try {
                    return TheMovieDbApi.instance.getMovieReview(id, page).list
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

    override fun onLoadFinished(loader: Loader<ArrayList<MovieReview>?>, movies: ArrayList<MovieReview>?) {
        if (movies != null) {
            mMovieDetail.reviewAdapter!!.moviesData = movies
            if (movies.size < 1) mMovieDetail.showMessage("Trailer list is empty yet")
        } else {
            mMovieDetail.showMessage(errorMessage)
        }
        LoaderManager.getInstance(mMovieDetail).destroyLoader(AsyncLoaderMovie_REVIEWS)
    }

    override fun onLoaderReset(loader: Loader<ArrayList<MovieReview>?>) {}

    companion object {
        private const val AsyncLoaderMovie_REVIEWS = 25
    }

    init {
        LoaderManager.getInstance(mMovieDetail).initLoader(AsyncLoaderMovie_REVIEWS, null, this)
        loadMovies(1)
    }
}