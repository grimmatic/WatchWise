    package com.vakifbank.WatchWise.ui.adapter

    import android.content.Context
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.vakifbank.WatchWise.R
    import com.vakifbank.WatchWise.domain.model.Movie
    import kotlin.String

    class SearchMovieAdapter(
        private val movieList: MutableList<Movie>,
        private val onMovieClick: (Movie) -> Unit = {}
    ) : RecyclerView.Adapter<SearchMovieAdapter.SearchMovieViewHolder>() {

        class SearchMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            //View Binding kullanabilir miyiz?
            val moviePoster: ImageView = itemView.findViewById(R.id.searchMoviePoster)
            val movieTitle: TextView = itemView.findViewById(R.id.searchMovieTitle)
            val movieRating: TextView = itemView.findViewById(R.id.searchMovieRating)
            val movieDescription: TextView = itemView.findViewById(R.id.searchMovieDetail)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_recycler_row, parent, false)
            return SearchMovieViewHolder(view)
        }

        override fun onBindViewHolder(holder: SearchMovieViewHolder, position: Int) {
            val movie = movieList[position]

            holder.movieTitle.text = movie.title
            holder.movieDescription.text = movie.description
            holder.movieRating.text = String.format("%.1f", movie.rating)

            val posterUrl = "https://image.tmdb.org/t/p/w500${movie.poster}"
            Glide.with(holder.itemView.context)
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.moviePoster)

            holder.itemView.setOnClickListener {
                onMovieClick(movie)
            }

            //son item için margin
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            if (position == movieList.size - 1) {
                layoutParams.bottomMargin = 60.dpToPx(holder.itemView.context)
            }
            holder.itemView.layoutParams = layoutParams
        }
        fun Int.dpToPx(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }
        override fun getItemCount(): Int {
            return movieList.size
        }

        fun updateMovies(newMovies: List<Movie>) {
            movieList.clear()
            movieList.addAll(newMovies)
            notifyDataSetChanged()
        }

        fun clearMovies() {
            movieList.clear()
            notifyDataSetChanged()
        }
    }