package com.vakifbank.WatchWise.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.domain.model.Movie

class MovieAdapter(
    private val movieList: List<Movie>,
    private val onMovieClick: (Movie) -> Unit = {}
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {


    private lateinit var rowType: LinearLayout

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filmImage: ImageView = itemView.findViewById(R.id.filmImage)
        val filmTitle: TextView = itemView.findViewById(R.id.filmTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_row, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.filmTitle.text = movie.title
        holder.filmImage.setImageResource(movie.image)
        holder.itemView.setOnClickListener {
            onMovieClick(movie)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}