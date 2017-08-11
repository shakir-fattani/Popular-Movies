package com.appsys.android.popularmovie;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsys.android.popularmovie.adapter.MovieAdapter;
import com.appsys.android.popularmovie.api.MovieType;
import com.appsys.android.popularmovie.api.TheMovieDbApi;
import com.appsys.android.popularmovie.classes.Movie;
import com.appsys.android.popularmovie.classes.MovieDbException;
import com.appsys.android.popularmovie.classes.SearchResult;
import com.appsys.android.popularmovie.data.MovieListContract;
import com.appsys.android.popularmovie.data.MovieListHelper;
import com.appsys.android.popularmovie.api.ViewEnum;
import com.appsys.android.popularmovie.view.EndlessRecyclerViewScrollListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    final static int AsyncLoaderMovie_ID = 22;
    RecyclerView mRecyclerView;
    MovieAdapter mMovieAdapter;
    TextView mErrorTextView;
    ProgressBar mProgressBar;
    int mTotalItems = 0;
    EndlessRecyclerViewScrollListener mScrollListener;
    Toast mToast;
    boolean mPopular = true;
    boolean mFavorite = false;
    MovieType mType = MovieType.Latest;
    String mErrorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mErrorTextView = (TextView) findViewById(R.id.error_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_view);

        GridLayoutManager glm = new GridLayoutManager(this, calculateNoOfColumns(), GridLayoutManager.VERTICAL, false);

        mScrollListener = new EndlessRecyclerViewScrollListener(glm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (!(mTotalItems > totalItemsCount))
                    return;
                if (mFavorite)
                    return;

                int pageNo = (totalItemsCount / 20) + 1;
                loadMovies(pageNo, mType);
            }
        };

        mRecyclerView.setLayoutManager(glm);

        mMovieAdapter = new MovieAdapter(this);
        mMovieAdapter.setMoviesData(new ArrayList<Movie>());
        mRecyclerView.setAdapter(mMovieAdapter);
        getLoaderManager().initLoader(AsyncLoaderMovie_ID, null, this);

        if (savedInstanceState == null || !savedInstanceState.containsKey("Movie"))
            loadMovies(1, MovieType.Upcoming);
        else {
            mTotalItems = savedInstanceState.getInt("totalItems");
            mType = MovieType.values[savedInstanceState.getInt("type")];
            mMovieAdapter.setMoviesData(savedInstanceState.<Movie>getParcelableArrayList("Movie"));
        }
        mRecyclerView.addOnScrollListener(mScrollListener);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(sp, "Preference");
    }

    private void loadMovies(int pageNo, MovieType type) {
        if (mFavorite && pageNo > 1)
            return;
        mType = type;

        Bundle b = new Bundle();
        b.putInt("type", type.ordinal());
        b.putInt("Page", pageNo);

        LoaderManager loaderManager = getLoaderManager();
        Loader<Movie[]> movieApi = loaderManager.getLoader(AsyncLoaderMovie_ID);
        if (movieApi == null) {
            loaderManager.initLoader(AsyncLoaderMovie_ID, b, this);
        } else {
            loaderManager.restartLoader(AsyncLoaderMovie_ID, b, this);
        }
    }

    private int calculateNoOfColumns() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float dpWidth = dm.widthPixels / dm.density;
        int imageWidth = 180;
        int noOfColumns = (int)(dpWidth / imageWidth);
        return noOfColumns;
    }

    @Override
    public void onClick(Movie m) {
        Intent i = new Intent(this, MovieDetail.class);
        i.putExtra(Intent.EXTRA_TEXT, m);
        startActivity(i);
    }

    public void showMessage(String message) {
        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id
        mFavorite = false;
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                mPopular = true;
                mMovieAdapter.resetMovieList();
                loadMovies(1, MovieType.Popular);
                return true;
            case R.id.action_sort_top_rated:
                mPopular = false;
                mMovieAdapter.resetMovieList();
                loadMovies(1, MovieType.TopRate);
                return true;
            case R.id.action_sort_favorite:
                mFavorite = true;
                mMovieAdapter.resetMovieList();
                loadMovies(1, MovieType.Favorite);
                return true;
            case R.id.action_sort_upcoming:
                mFavorite = true;
                mMovieAdapter.resetMovieList();
                loadMovies(1, MovieType.Upcoming);
                return true;
            case R.id.action_setting:
                Intent i = new Intent(this, SettingActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUI(ViewEnum e) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);

        switch (e) {
            case Data:
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case Progress:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            default:
                mRecyclerView.setVisibility(View.INVISIBLE);
                mErrorTextView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Movie", mMovieAdapter.getMoviesData());
        outState.putInt("type", mType.ordinal());
        outState.putInt("totalItems", mTotalItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, final Bundle bundle) {
        Log.d("mainactivity", "console.log ----- oncreate");
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null)
                    return;
                setUI(ViewEnum.Progress);
                if (!takeContentChanged())
                    forceLoad();
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                MovieType type = MovieType.values[bundle.getInt("type")];

                if (type == MovieType.Favorite) {
                    MovieListHelper mlh = new MovieListHelper(MainActivity.this);
                    SQLiteDatabase db = mlh.getReadableDatabase();

                    Cursor c = db.query(MovieListContract.MovieListEntry.TABLE_NAME, null, null, null, null, null, MovieListContract.MovieListEntry._ID);
                    mTotalItems = c.getCount();

                    ArrayList<Movie> movieArrayList = new ArrayList<Movie>();
                    if (c.getCount() > 0) {
                        while (c.moveToNext()) {
                            Movie movie = new Movie(
                                    c.getInt(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_ID)),
                                    c.getString(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_TITLE)),
                                    c.getString(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_POSTER)),
                                    c.getString(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_BACKDROP)),
                                    c.getString(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_OVERVIEW)),
                                    c.getString(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_RATING)),
                                    c.getString(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_LANGUAGE)),
                                    c.getString(c.getColumnIndex(MovieListContract.MovieListEntry.COLUMN_RELEASE))
                            );
                            movieArrayList.add(movie);
                        }
                    } else {
                        mErrorMessage = "There is no local data yet";
                    }
                    c.close();
                    return movieArrayList;
                }
//                if (bundle.getBoolean("favorite", false)) {
//                }

                int page = bundle.getInt("Page");

                try {
                    TheMovieDbApi api = TheMovieDbApi.getInstance();

                    SearchResult<Movie> srm;
                    if (type == MovieType.Popular)
                        srm = api.getPopularMovies(page);
                    else if (type == MovieType.Upcoming)
                        srm = api.getUpcomingMovies(page);
                    else if (type == MovieType.TopRate)
                        srm = api.getTopRatedMovies(page);
                    else
                        srm = api.getUpcomingMovies(page);

                    mTotalItems = srm.getTotalResult();

                    return srm.getList();

                } catch (MovieDbException e) {
                    mErrorMessage = e.getMessage();
                    e.printStackTrace();
                } catch (IOException e) {
                    mErrorMessage = "Please check your Internet connection";
                    e.printStackTrace();
                } catch (Exception e) {
                    mErrorMessage = "Main: " + e.getMessage();
                    e.printStackTrace();
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        if (movies == null) {
            if (mMovieAdapter.getItemCount() < 1)
                setUI(ViewEnum.Error);
            showMessage(mErrorMessage);
        } else {
            setUI(ViewEnum.Data);
            mMovieAdapter.setMoviesData(movies);
        }
        getLoaderManager().destroyLoader(AsyncLoaderMovie_ID);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("Preference"))
            TheMovieDbApi.getInstance().setApiKey(sharedPreferences.getString("Preference", ""));
    }
}
