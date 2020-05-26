package com.appsys.android.popularmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appsys.android.popularmovie.classes.MovieReview
import com.shakirfattani.course.movielisting.R
import kotlinx.android.synthetic.main.movie_reviews_item.view.*
import java.util.*

/**
 * Created by shakir on 8/2/2017.
 */
class ReviewAdapter(val mMovies: ArrayList<MovieReview> = arrayListOf()) : RecyclerView.Adapter<ReviewAdapter.TrailerAdapterViewHolder>() {
    fun resetMovieList() {
        mMovies.clear()
        notifyDataSetChanged()
    }

    var moviesData: ArrayList<MovieReview>?
        get() = mMovies
        set(movieArray) {
            mMovies.addAll(movieArray!!)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    =TrailerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_reviews_item, parent, false))


    override fun onBindViewHolder(holder: TrailerAdapterViewHolder, position: Int) {
        holder.bind(mMovies[position])
    }

    override fun getItemCount() = mMovies.size

    inner class TrailerAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        fun bind(m: MovieReview) {
            view.reviewer_name.text = m.author
            view.review.text = m.message
        }

        override fun onClick(view: View) {
            val adapterPosition = adapterPosition
            val m = mMovies[adapterPosition]
        }

        init {
            view.setOnClickListener(this)
        }
    }
}