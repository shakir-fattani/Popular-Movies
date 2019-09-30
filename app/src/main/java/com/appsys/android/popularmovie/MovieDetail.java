package com.appsys.android.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsys.android.popularmovie.adapter.ReviewAdapter;
import com.appsys.android.popularmovie.adapter.TrailerAdapter;
import com.appsys.android.popularmovie.api.TheMovieDbApi;
import com.appsys.android.popularmovie.callbackHandlers.MovieReviews;
import com.appsys.android.popularmovie.callbackHandlers.MovieTrailers;
import com.appsys.android.popularmovie.classes.Movie;
import com.appsys.android.popularmovie.classes.MovieTrailer;
import com.appsys.android.popularmovie.data.MovieListContract;
import com.appsys.android.popularmovie.data.MovieListHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, TrailerAdapter.TrailerAdapterOnClickHandler {

    TextView mReleaseTextView;
    TextView mRatingTextView;
    TextView mOverviewTextView;
    ImageView mPoster;
    Toast mToast;

    RecyclerView mRecyclerView;
    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;
    SQLiteDatabase mDb;
    Movie mMovie;

    public TrailerAdapter getTrailerAdapter() {
        return mTrailerAdapter;
    }
    public ReviewAdapter getReviewAdapter() {
        return mReviewAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mReleaseTextView = findViewById(R.id.detail_release);
        mRatingTextView =  findViewById(R.id.rating);
        mOverviewTextView =  findViewById(R.id.detail_overview);
        mPoster =  findViewById(R.id.expandedImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_toolbar_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(MovieDetail.this);
            }
        });
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(sp, "Preference");

        Intent i = getIntent();
        if (i != null && i.hasExtra(Intent.EXTRA_TEXT)) {
            Movie m = i.getParcelableExtra(Intent.EXTRA_TEXT);
            if (m != null) {
                mMovie = m;
                setTitle(m.getTitle());
                Picasso.with(this).load(m.getBackdrop()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.not_found).into(mPoster);
                mOverviewTextView.setText(m.getOverview());
                mRatingTextView.setText("Rating: " + m.getRating() + "/10");
                mReleaseTextView.setText("Release: " + m.getRelease());

                mRecyclerView = (RecyclerView) findViewById(R.id.detail_recycle);
                LinearLayoutManager llm = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(llm);
                mRecyclerView.setHasFixedSize(true);
                mTrailerAdapter = new TrailerAdapter(this);
                mReviewAdapter = new ReviewAdapter();
                mRecyclerView.setAdapter(mTrailerAdapter);
                new MovieTrailers(m.getId(), this);

                MovieListHelper mlh = new MovieListHelper(this);
                mDb = mlh.getWritableDatabase();
                setFavoriate(m);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    public void showMessage(String message) {
        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }


    private void setFavoriate(final Movie m) {
        Cursor c = mDb.query(MovieListContract.MovieListEntry.TABLE_NAME, null, MovieListContract.MovieListEntry.COLUMN_ID + "=" + m.getId(), null, null, null, MovieListContract.MovieListEntry.COLUMN_ID);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (c.getCount() > 0) {
            fab.setImageResource(android.R.drawable.btn_star_big_on);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String stringId = Integer.toString(m.getId());
                    Uri uri = MovieListContract.MovieListEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringId).build();
                    getContentResolver().delete(uri, null, null);
                    setFavoriate(m);
                }
            });
        } else {
            fab.setImageResource(android.R.drawable.btn_star_big_off);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues cv = new ContentValues();
                    cv.put(MovieListContract.MovieListEntry.COLUMN_ID, String.valueOf(m.getId()));
                    cv.put(MovieListContract.MovieListEntry.COLUMN_TITLE, m.getTitle());
                    cv.put(MovieListContract.MovieListEntry.COLUMN_POSTER, m.getPosterData());
                    cv.put(MovieListContract.MovieListEntry.COLUMN_BACKDROP, m.getBackdropData());
                    cv.put(MovieListContract.MovieListEntry.COLUMN_OVERVIEW, m.getOverview());
                    cv.put(MovieListContract.MovieListEntry.COLUMN_RATING, m.getRating());
                    cv.put(MovieListContract.MovieListEntry.COLUMN_RELEASE, m.getRelease());
                    cv.put(MovieListContract.MovieListEntry.COLUMN_LANGUAGE, m.getLanguage());

                    Uri uri = getContentResolver().insert(MovieListContract.MovieListEntry.CONTENT_URI, cv);
                    setFavoriate(m);
                }
            });
        }
        c.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_set_trailers:
                if (mTrailerAdapter.getItemCount() < 1)
                    new MovieTrailers(mMovie.getId(), this);
                mRecyclerView.setAdapter(mTrailerAdapter);
                return true;
            case R.id.action_set_reviews:
                if (mReviewAdapter.getItemCount() < 1)
                    new MovieReviews(mMovie.getId(), this);
                mRecyclerView.setAdapter(mReviewAdapter);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(MovieTrailer m) {
        Intent i = new Intent(Intent.ACTION_VIEW, m.getVideoUrl());
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("Preference"))
            TheMovieDbApi.getInstance().setApiKey(sharedPreferences.getString("Preference", ""));
    }
}
