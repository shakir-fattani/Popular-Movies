package com.appsys.android.popularmovie.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsys.android.popularmovie.R;
import com.appsys.android.popularmovie.classes.MovieReview;

import java.util.ArrayList;

/**
 * Created by shakir on 8/2/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.TrailerAdapterViewHolder> {

    private ArrayList<MovieReview> mMovies;

    public ReviewAdapter() {
        mMovies = new ArrayList<MovieReview>();
    }

    public void resetMovieList() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    public void setMoviesData(ArrayList<MovieReview> movieArray) {
        mMovies.addAll(movieArray);
        notifyDataSetChanged();
    }

    public ArrayList<MovieReview> getMoviesData() {
        return mMovies;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_reviews_item, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mAuthor;
        public final TextView mReview;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mAuthor = (TextView) view.findViewById(R.id.reviewer_name);
            mReview = (TextView) view.findViewById(R.id.review);
            view.setOnClickListener(this);
        }

        void bind(MovieReview m) {
            mAuthor.setText(m.getAuthor());
            mReview.setText(m.getMessage());
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieReview m = mMovies.get(adapterPosition);
       }
    }
}
