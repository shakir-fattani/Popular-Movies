package com.appsys.android.popularmovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appsys.android.popularmovie.classes.MovieTrailer
import com.shakirfattani.course.movielisting.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_trailer_item.view.*
import java.util.*

/**
 * Created by shakir on 8/2/2017.
 */
class TrailerAdapter(private val mOnClickHandler: TrailerAdapterOnClickHandler, val moviesData: ArrayList<MovieTrailer> = arrayListOf()) : RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>() {

    fun resetMovieList() {
        moviesData.clear()
        notifyDataSetChanged()
    }

    fun setMoviesData(movieArray: ArrayList<MovieTrailer>) {
        moviesData.addAll(movieArray)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            TrailerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_trailer_item, parent, false))

    override fun onBindViewHolder(holder: TrailerAdapterViewHolder, position: Int) = holder.bind(moviesData[position])

    override fun getItemCount() = moviesData.size

    interface TrailerAdapterOnClickHandler {
        fun onClick(m: MovieTrailer?)
    }

    inner class TrailerAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        fun bind(m: MovieTrailer) {
            itemView.trailer_title.text = m.title
            Picasso.with(itemView.context).load(m.imageUrl).placeholder(R.mipmap.ic_launcher).error(R.mipmap.not_found).into(itemView.trailer_poster)
        }

        override fun onClick(view: View) {
            val adapterPosition = adapterPosition
            val m = moviesData[adapterPosition]
            mOnClickHandler.onClick(m)
        }

        init {
            view.setOnClickListener(this)
        }
    }
}