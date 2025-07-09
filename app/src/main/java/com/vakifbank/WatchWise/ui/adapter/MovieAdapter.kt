package com.vakifbank.WatchWise.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.RecyclerRowBinding
import com.vakifbank.WatchWise.domain.model.Movie

enum class MovieListType {
    POPULAR, TOP_RATED, UPCOMING
}

class MovieAdapter(
    private val movieList: List<Movie>,
    private val listType: MovieListType = MovieListType.POPULAR,
    private val onMovieClick: (Movie) -> Unit = {}
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(private val binding: RecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie, listType: MovieListType, onMovieClick: (Movie) -> Unit) {
            binding.filmTitle.text = movie.title ?: "Bilinmeyen Film"

            setBackgroundByType(listType)

            loadMoviePoster(movie.poster)

            binding.root.setOnClickListener {
                onMovieClick(movie)
            }
        }

        private fun setBackgroundByType(listType: MovieListType) {
            val backgroundResource = when (listType) {
                MovieListType.POPULAR -> R.drawable.rounded_bg_popular
                MovieListType.TOP_RATED -> R.drawable.rounded_bg_top_rated
                MovieListType.UPCOMING -> R.drawable.rounded_bg_upcoming
            }
            binding.containerLayout.setBackgroundResource(backgroundResource)
        }

        private fun loadMoviePoster(posterPath: String?) {
            val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
            Glide.with(binding.root.context)
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(binding.filmImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = RecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie, listType, onMovieClick)
    }

    override fun getItemCount(): Int = movieList.size
}