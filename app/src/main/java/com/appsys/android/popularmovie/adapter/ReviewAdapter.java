package com.appsys.android.popularmovie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.appsys.android.popularmovie.classes.MovieReview;
import com.shakirfattani.course.movielisting.R;

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

    public ArrayList<MovieReview> getMoviesData() {
        return mMovies;
    }

    public void setMoviesData(ArrayList<MovieReview> movieArray) {
        mMovies.addAll(movieArray);
        notifyDataSetChanged();
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
            mAuthor = view.findViewById(R.id.reviewer_name);
            mReview = view.findViewById(R.id.review);
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
