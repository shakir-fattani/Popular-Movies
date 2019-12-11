package com.appsys.android.popularmovie

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appsys.android.popularmovie.adapter.MovieAdapter
import com.appsys.android.popularmovie.api.MovieType
import com.appsys.android.popularmovie.api.TheMovieDbApi
import com.appsys.android.popularmovie.api.ViewEnum
import com.appsys.android.popularmovie.classes.Movie
import com.appsys.android.popularmovie.classes.MovieDbException
import com.appsys.android.popularmovie.classes.SearchResult
import com.appsys.android.popularmovie.data.MovieListContract
import com.appsys.android.popularmovie.data.MovieListHelper
import com.appsys.android.popularmovie.view.EndlessRecyclerViewScrollListener
import com.shakirfattani.course.movielisting.R
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    var mRecyclerView: RecyclerView? = null
    var mMovieAdapter: MovieAdapter? = null
    var mErrorTextView: TextView? = null
    var mProgressBar: ProgressBar? = null
    var mTotalItems = 0
    var mScrollListener: EndlessRecyclerViewScrollListener? = null
    var mToast: Toast? = null
    var mPopular = true
    var mFavorite = false
    var mType = MovieType.Latest
    var mErrorMessage: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        mErrorTextView = findViewById<View>(R.id.error_view) as TextView
        mProgressBar = findViewById<View>(R.id.progress_view) as ProgressBar

        val glm = GridLayoutManager(this, calculateNoOfColumns(), GridLayoutManager.VERTICAL, false)

        mScrollListener = object : EndlessRecyclerViewScrollListener(glm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (mTotalItems <= totalItemsCount)
                    return
                if (mFavorite)
                    return

                val pageNo = totalItemsCount / 20 + 1
                loadMovies(pageNo, mType)
            }
        }

        mRecyclerView?.layoutManager = glm

        mMovieAdapter = MovieAdapter(this)
        mMovieAdapter?.moviesData = ArrayList()
        mRecyclerView?.adapter = mMovieAdapter
        LoaderManager.getInstance(this).initLoader(AsyncLoaderMovie_ID, null, this)

        if (savedInstanceState == null || !savedInstanceState.containsKey("Movie")) {
            loadMovies(1, MovieType.Upcoming)
            title = "Upcoming Movies"
        } else {
            // there is a already save movie data because of rotation
            mTotalItems = savedInstanceState.getInt("totalItems")
            mType = savedInstanceState.getInt("type")
            mMovieAdapter?.moviesData = savedInstanceState.getParcelableArrayList("Movie")
        }
        mRecyclerView?.addOnScrollListener(mScrollListener!!)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.registerOnSharedPreferenceChangeListener(this)
        onSharedPreferenceChanged(sp, "Preference")
    }

    private fun loadMovies(pageNo: Int, type: Int) {
        if (mFavorite && pageNo > 1)
            return
        mType = type

        val b = Bundle()
        b.putInt("type", type)
        b.putInt("Page", pageNo)

        //        Loader<Movie[]> movieApi = loaderManager.getLoader(AsyncLoaderMovie_ID);
        //        if (movieApi == null) {
        //            loaderManager.initLoader(AsyncLoaderMovie_ID, b, this);
        //        } else {
        LoaderManager.getInstance(this).restartLoader(AsyncLoaderMovie_ID, b, this)
        //        }
    }

    private fun calculateNoOfColumns(): Int {
        val dm = resources.displayMetrics
        val dpWidth = dm.widthPixels / dm.density
        val imageWidth = 180
        return (dpWidth / imageWidth).toInt()
    }

    override fun onClick(m: Movie) = startActivity(Intent(this, MovieDetail::class.java).putExtra(Intent.EXTRA_TEXT, m))

    fun showMessage(message: String?) {
        if (mToast != null)
            mToast!!.cancel()

        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        mToast!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //        int id
        mFavorite = false
        when (item.itemId) {
            R.id.action_sort_popular -> {
                mPopular = true
                title = "Popular Movies"
                mMovieAdapter?.resetMovieList()
                mArrayList.add(MovieType.Popular)
                loadMovies(1, MovieType.Popular)
                return true
            }
            R.id.action_sort_top_rated -> {
                mPopular = false
                title = "Top Rated Movies"
                mMovieAdapter?.resetMovieList()
                mArrayList.add(MovieType.TopRate)
                loadMovies(1, MovieType.TopRate)
                return true
            }
            R.id.action_sort_favorite -> {
                mFavorite = true
                title = "Favorite Movies"
                mMovieAdapter?.resetMovieList()
                mArrayList.add(MovieType.Favorite)
                loadMovies(1, MovieType.Favorite)
                return true
            }
            R.id.action_sort_upcoming -> {
                mFavorite = true
                title = "Upcoming Movies"
                mMovieAdapter?.resetMovieList()
                mArrayList.add(MovieType.Upcoming)
                loadMovies(1, MovieType.Upcoming)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //    private

    private fun setUI(e: ViewEnum) {
        mProgressBar?.visibility = View.INVISIBLE
        mErrorTextView?.visibility = View.INVISIBLE

        when (e) {
            ViewEnum.Data -> mRecyclerView?.visibility = View.VISIBLE
            ViewEnum.Progress -> mProgressBar?.visibility = View.VISIBLE
            else -> {
                mRecyclerView?.visibility = View.INVISIBLE
                mErrorTextView?.visibility = View.VISIBLE
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("Movie", mMovieAdapter?.moviesData)
        outState.putInt("type", mType)
        outState.putInt("totalItems", mTotalItems)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<ArrayList<Movie>> {
        Log.d("mainactivity", "console.log ----- oncreate")
        return object : AsyncTaskLoader<ArrayList<Movie>>(this) {
            override fun onStartLoading() {
                super.onStartLoading()
                if (bundle == null)
                    return
                setUI(ViewEnum.Progress)
                if (!takeContentChanged())
                    forceLoad()
            }

            override fun loadInBackground(): ArrayList<Movie>? {
                val type = bundle!!.getInt("type")

                if (type == MovieType.Favorite) {
                    val c = contentResolver.query(MovieListContract.MovieListEntry.CONTENT_URI, null, null, null, MovieListContract.MovieListEntry._ID)
                    mTotalItems = c!!.count

                    val movieArrayList = ArrayList<Movie>()
                    if (c.count > 0) {
                        val idCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_ID)
                        val titleCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_TITLE)
                        val posterCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_POSTER)
                        val backdropCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_BACKDROP)
                        val overviewCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_OVERVIEW)
                        val ratingCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_RATING)
                        val langCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_LANGUAGE)
                        val releaseCol = c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_RELEASE)
                        while (c.moveToNext()) {
                            movieArrayList.add(Movie(
                                    c.getInt(idCol),
                                    c.getString(titleCol),
                                    c.getString(posterCol),
                                    c.getString(backdropCol),
                                    c.getString(overviewCol),
                                    c.getString(ratingCol),
                                    c.getString(langCol),
                                    c.getString(releaseCol)
                            ))
                        }
                    } else {
                        mErrorMessage = "There is no local data yet"
                    }
                    c.close()
                    return movieArrayList
                }

                val page = bundle.getInt("Page")

                try {
                    val api = TheMovieDbApi.instance

                    val srm: SearchResult<Movie>
                    if (type == MovieType.Popular)
                        srm = api.getPopularMovies(page)
                    else if (type == MovieType.Upcoming)
                        srm = api.getUpcomingMovies(page)
                    else if (type == MovieType.TopRate)
                        srm = api.getTopRatedMovies(page)
                    else
                        srm = api.getUpcomingMovies(page)

                    mTotalItems = srm.totalResult

                    return srm.list

                } catch (e: MovieDbException) {
                    mErrorMessage = e.message
                    e.printStackTrace()
                } catch (e: IOException) {
                    mErrorMessage = "Please check your Internet connection"
                    e.printStackTrace()
                } catch (e: Exception) {
                    mErrorMessage = "Main: " + e.message
                    e.printStackTrace()
                }

                return null
            }
        }
    }

    override fun onLoadFinished(loader: Loader<ArrayList<Movie>>, movies: ArrayList<Movie>?) {
        if (movies == null) {
            if (mMovieAdapter!!.itemCount < 1)
                setUI(ViewEnum.Error)
            showMessage(mErrorMessage)
        } else {
            setUI(ViewEnum.Data)
            mMovieAdapter?.moviesData = movies
        }
        LoaderManager.getInstance(this).destroyLoader(AsyncLoaderMovie_ID)
    }

    override fun onLoaderReset(loader: Loader<ArrayList<Movie>>) {

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "Preference")
            TheMovieDbApi.instance.apikey = sharedPreferences.getString("Preference", "479407e3eb2f80c4ee8f711ffaa9cb63") ?: "479407e3eb2f80c4ee8f711ffaa9cb63"
    }

    companion object {
        const val AsyncLoaderMovie_ID = 22
        private val mArrayList = ArrayList<Int>()
    }
}
