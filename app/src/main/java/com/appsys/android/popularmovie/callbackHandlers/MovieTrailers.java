package com.appsys.android.popularmovie.callbackHandlers;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;

import com.appsys.android.popularmovie.MovieDetail;
import com.appsys.android.popularmovie.api.TheMovieDbApi;
import com.appsys.android.popularmovie.classes.Movie;
import com.appsys.android.popularmovie.classes.MovieDbException;
import com.appsys.android.popularmovie.classes.MovieTrailer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by shakir on 8/8/2017.
 */

public class MovieTrailers  implements LoaderManager.LoaderCallbacks<ArrayList<MovieTrailer>> {

    private final static int AsyncLoaderMovie_TRAILER = 26;

    private String mErrorMessage = "";
    private int mMovieId;
    private MovieDetail mMovieDetail;

    public MovieTrailers(int movieId, MovieDetail movieDetail) {
        mMovieId = movieId;
        mMovieDetail = movieDetail;
        mMovieDetail.getLoaderManager().initLoader(mMovieId, null, this);
        loadMovies();
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    private void loadMovies() {
        Bundle b = new Bundle();
        b.putInt("MovieId", mMovieId);

        LoaderManager loaderManager = mMovieDetail.getLoaderManager();
        Loader<Movie[]> movieApi = loaderManager.getLoader(AsyncLoaderMovie_TRAILER);
        if (movieApi == null) {
            loaderManager.initLoader(AsyncLoaderMovie_TRAILER, b, this);
        } else {
            loaderManager.restartLoader(AsyncLoaderMovie_TRAILER, b, this);
        }
    }

    @Override
    public Loader<ArrayList<MovieTrailer>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<MovieTrailer>>(mMovieDetail) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null)
                    return;
                forceLoad();
            }

            @Override
            public ArrayList<MovieTrailer> loadInBackground() {
                int id = bundle.getInt("MovieId");

                try {
                    return TheMovieDbApi.getInstance().getMovieTrailer(id);
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
    public void onLoadFinished(Loader<ArrayList<MovieTrailer>> loader, ArrayList<MovieTrailer> movies) {
        if (movies != null) {
            mMovieDetail.getTrailerAdapter().setMoviesData(movies);
            if (movies.size() < 1)
                mMovieDetail.showMessage("Trailer list is empty yet");
        } else {
            mMovieDetail.showMessage(mErrorMessage);
        }
        mMovieDetail.getLoaderManager().destroyLoader(AsyncLoaderMovie_TRAILER);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieTrailer>> loader) {

    }
}
