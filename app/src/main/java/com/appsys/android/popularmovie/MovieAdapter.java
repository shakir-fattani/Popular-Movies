package com.appsys.android.popularmovie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsys.android.popularmovie.classes.Movie;
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
    }

    public void setMoviesData(ArrayList<Movie> movieArray) {
        mMovies = movieArray;
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMoviesData() {
        return mMovies;
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

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.image_thumb);
            view.setOnClickListener(this);
        }

        void bind(Movie m) {
            Picasso p = Picasso.with(mImageView.getContext());
//            p.setIndicatorsEnabled(true);
            p.load(m.getPoster()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.not_found).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie m = mMovies.get(adapterPosition);
            mOnClickHandler.onClick(m);
       }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie m);
    }
}
