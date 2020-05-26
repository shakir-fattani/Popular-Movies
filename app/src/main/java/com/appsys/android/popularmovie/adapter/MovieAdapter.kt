package com.appsys.android.popularmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.appsys.android.popularmovie.adapter.MovieAdapter.MovieAdapterViewHolder
import com.appsys.android.popularmovie.classes.Movie
import com.shakirfattani.course.movielisting.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_items.view.*
import java.util.*

/**
 * Created by shakir on 8/2/2017.
 */
class MovieAdapter(private val mOnClickHandler: MovieAdapterOnClickHandler, private val mMovies: ArrayList<Movie> = ArrayList()) : RecyclerView.Adapter<MovieAdapterViewHolder>() {
    fun resetMovieList() {
        mMovies.clear()
        notifyDataSetChanged()
    }

    var moviesData: ArrayList<Movie>
        get() = mMovies
        set(movieArray) {
            mMovies.addAll(movieArray)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MovieAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_items, parent, false))

    override fun onBindViewHolder(holder: MovieAdapterViewHolder, position: Int)
        = holder.bind(mMovies[position])

    override fun getItemCount() = mMovies.size

    interface MovieAdapterOnClickHandler {
        fun onClick(m: Movie)
    }

    inner class MovieAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        fun bind(m: Movie) {
            Picasso.with(view.context)
                    .load(m.poster)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.not_found)
                    .into(view.image_thumb)
        }

        override fun onClick(view: View) {
            mOnClickHandler.onClick(mMovies[adapterPosition])
        }

        init {
            view.setOnClickListener(this)
        }
    }

}