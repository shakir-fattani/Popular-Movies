package com.appsys.android.popularmovie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.appsys.android.popularmovie.classes.Movie;
import com.shakirfattani.course.movielisting.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shakir on 8/2/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovies;
    private MovieAdapterOnClickHandler mOnClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler onClick) {
        mOnClickHandler = onClick;
        mMovies = new ArrayList<Movie>();
    }

    public void resetMovieList() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMoviesData() {
        return mMovies;
    }

    public void setMoviesData(ArrayList<Movie> movieArray) {
        mMovies.addAll(movieArray);
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_items, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie m);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.image_thumb);
            view.setOnClickListener(this);
        }

        void bind(Movie m) {
            Picasso p = Picasso.with(mImageView.getContext());
            p.load(m.getPoster()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.not_found).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie m = mMovies.get(adapterPosition);
            mOnClickHandler.onClick(m);
        }
    }
}
