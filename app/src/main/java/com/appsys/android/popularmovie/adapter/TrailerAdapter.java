package com.appsys.android.popularmovie.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsys.android.popularmovie.R;
import com.appsys.android.popularmovie.classes.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shakir on 8/2/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<MovieTrailer> mMovies;
    private TrailerAdapterOnClickHandler mOnClickHandler;

    public TrailerAdapter(TrailerAdapterOnClickHandler onClick) {
        mOnClickHandler = onClick;
        mMovies = new ArrayList<MovieTrailer>();
    }

    public void resetMovieList() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    public void setMoviesData(ArrayList<MovieTrailer> movieArray) {
        mMovies.addAll(movieArray);
        notifyDataSetChanged();
    }

    public ArrayList<MovieTrailer> getMoviesData() {
        return mMovies;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_trailer_item, parent, false);
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
        public final ImageView mImageView;
        public final TextView mTitle;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.trailer_poster);
            mTitle = (TextView) view.findViewById(R.id.trailer_title);
            view.setOnClickListener(this);
        }

        void bind(MovieTrailer m) {
            mTitle.setText(m.getTitle());
            Picasso p = Picasso.with(mImageView.getContext());
            p.load(m.getImageUrl()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.not_found).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieTrailer m = mMovies.get(adapterPosition);
            mOnClickHandler.onClick(m);
       }
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(MovieTrailer m);
    }
}
