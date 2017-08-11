package com.appsys.android.popularmovie.callbackHandlers;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;

import com.appsys.android.popularmovie.MovieDetail;
import com.appsys.android.popularmovie.api.TheMovieDbApi;
import com.appsys.android.popularmovie.classes.MovieDbException;
import com.appsys.android.popularmovie.classes.MovieReview;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by shakir on 8/8/2017.
 */

public class MovieReviews implements LoaderManager.LoaderCallbacks<ArrayList<MovieReview>> {

    private final static int AsyncLoaderMovie_REVIEWS = 25;

    private String mErrorMessage = "";
    private int mMovieId;
    private MovieDetail mMovieDetail;

    public MovieReviews(int movieId, MovieDetail movieDetail) {
        mMovieId = movieId;
        mMovieDetail = movieDetail;
        mMovieDetail.getLoaderManager().initLoader(AsyncLoaderMovie_REVIEWS, null, this);
        loadMovies(1);
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    private void loadMovies(int pageNo) {
        Bundle b = new Bundle();
        b.putInt("MovieId", mMovieId);
        b.putInt("Page", pageNo);

        LoaderManager loaderManager = mMovieDetail.getLoaderManager();
        if (loaderManager.getLoader(AsyncLoaderMovie_REVIEWS) == null) {
            loaderManager.initLoader(AsyncLoaderMovie_REVIEWS, b, this);
        } else {
            loaderManager.restartLoader(AsyncLoaderMovie_REVIEWS, b, this);
        }
    }

    @Override
    public Loader<ArrayList<MovieReview>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<MovieReview>>(mMovieDetail) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null)
                    return;
                forceLoad();
            }

            @Override
            public ArrayList<MovieReview> loadInBackground() {
                boolean popular = bundle.getBoolean("Popular");
                int page = bundle.getInt("Page");
                int id = bundle.getInt("MovieId");

                try {
                    return TheMovieDbApi.getInstance().getMovieReview(id, page).getList();
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
    public void onLoadFinished(Loader<ArrayList<MovieReview>> loader, ArrayList<MovieReview> movies) {
        if (movies != null) {
            mMovieDetail.getReviewAdapter().setMoviesData(movies);
            if (movies.size() < 1)
                mMovieDetail.showMessage("Trailer list is empty yet");
        } else {
            mMovieDetail.showMessage(mErrorMessage);
        }
        mMovieDetail.getLoaderManager().destroyLoader(AsyncLoaderMovie_REVIEWS);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieReview>> loader) {

    }
}
