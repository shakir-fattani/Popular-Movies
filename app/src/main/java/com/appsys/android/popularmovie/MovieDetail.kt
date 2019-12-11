package com.appsys.android.popularmovie

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsys.android.popularmovie.adapter.ReviewAdapter
import com.appsys.android.popularmovie.adapter.TrailerAdapter
import com.appsys.android.popularmovie.adapter.TrailerAdapter.TrailerAdapterOnClickHandler
import com.appsys.android.popularmovie.api.TheMovieDbApi
import com.appsys.android.popularmovie.callbackHandlers.MovieReviews
import com.appsys.android.popularmovie.callbackHandlers.MovieTrailers
import com.appsys.android.popularmovie.classes.Movie
import com.appsys.android.popularmovie.classes.MovieTrailer
import com.appsys.android.popularmovie.data.MovieListContract
import com.appsys.android.popularmovie.data.MovieListHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.shakirfattani.course.movielisting.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.movie_detail.*

class MovieDetail : AppCompatActivity(), OnSharedPreferenceChangeListener, TrailerAdapterOnClickHandler {
    var mToast: Toast? = null
    var trailerAdapter: TrailerAdapter? = null
    var reviewAdapter: ReviewAdapter? = null
    var db: SQLiteDatabase? = null
    var mMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.mipmap.ic_toolbar_arrow)
        toolbar.setNavigationOnClickListener { NavUtils.navigateUpFromSameTask(this@MovieDetail) }
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.registerOnSharedPreferenceChangeListener(this)
        onSharedPreferenceChanged(sp, "Preference")
        val i = intent
        if (i != null && i.hasExtra(Intent.EXTRA_TEXT)) {
            val m = i.getParcelableExtra<Movie>(Intent.EXTRA_TEXT)
            if (m != null) {
                mMovie = m
                title = m.title
                Picasso.with(this).load(m.backdrop).placeholder(R.mipmap.ic_launcher).error(R.mipmap.not_found).into(expandedImage)
                detail_overview.text = m.overview
                rating.text = "Rating: ${m.rating}/10"
                detail_release.text = "Release: ${m.release}"
                val llm = LinearLayoutManager(this)
                detail_recycle.layoutManager = llm
                detail_recycle.setHasFixedSize(true)
                trailerAdapter = TrailerAdapter(this)
                reviewAdapter = ReviewAdapter()
                detail_recycle.adapter = trailerAdapter
                MovieTrailers(m.id, this)
                val mlh = MovieListHelper(this)
                db = mlh.writableDatabase
                setFavoriate(m)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    fun showMessage(message: String?) {
        mToast?.cancel()
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
            show()
        }
    }

    private fun setFavoriate(m: Movie) {
        val c = db!!.query(MovieListContract.MovieListEntry.TABLE_NAME, null, MovieListContract.MovieListEntry.COLUMN_ID + "=" + m.id, null, null, null, MovieListContract.MovieListEntry.COLUMN_ID)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        if (c.count > 0) {
            fab.setImageResource(android.R.drawable.btn_star_big_on)
            fab.setOnClickListener {
                val stringId = Integer.toString(m.id)
                contentResolver.delete(MovieListContract.MovieListEntry.CONTENT_URI.buildUpon().appendPath(stringId).build(), null, null)
                setFavoriate(m)
            }
        } else {
            fab.setImageResource(android.R.drawable.btn_star_big_off)
            fab.setOnClickListener {
                contentResolver.insert(MovieListContract.MovieListEntry.CONTENT_URI, ContentValues().apply {
                    put(MovieListContract.MovieListEntry.COLUMN_ID, m.id.toString())
                    put(MovieListContract.MovieListEntry.COLUMN_TITLE, m.title)
                    put(MovieListContract.MovieListEntry.COLUMN_POSTER, m.posterPath)
                    put(MovieListContract.MovieListEntry.COLUMN_BACKDROP, m.backdropPath)
                    put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW, m.overview)
                    put(MovieListContract.MovieListEntry.COLUMN_RATING, m.rating)
                    put(MovieListContract.MovieListEntry.COLUMN_RELEASE, m.release)
                    put(MovieListContract.MovieListEntry.COLUMN_LANGUAGE, m.language)
                })
                setFavoriate(m)
            }
        }
        c.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_set_trailers -> {
                if (trailerAdapter!!.itemCount < 1) MovieTrailers(mMovie!!.id, this)
                detail_recycle.adapter = trailerAdapter
                true
            }
            R.id.action_set_reviews -> {
                if (reviewAdapter!!.itemCount < 1) MovieReviews(mMovie!!.id, this)
                detail_recycle.adapter = reviewAdapter
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(m: MovieTrailer?) =
            Intent(Intent.ACTION_VIEW, m?.videoUrl).run {
                if (resolveActivity(packageManager) != null)
                    startActivity(this@run)
            }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        TheMovieDbApi.instance.apikey = sharedPreferences.getString("Preference", "479407e3eb2f80c4ee8f711ffaa9cb63") ?: "479407e3eb2f80c4ee8f711ffaa9cb63"
    }
}