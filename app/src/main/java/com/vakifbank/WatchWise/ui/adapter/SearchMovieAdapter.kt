package com.vakifbank.WatchWise.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.SearchRecyclerRowBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.utils.toRatingString

class SearchMovieAdapter(
    private val movieList: MutableList<Movie>,
    private val onMovieClick: (Movie) -> Unit = {}
) : RecyclerView.Adapter<SearchMovieAdapter.SearchMovieViewHolder>() {

    class SearchMovieViewHolder(private val binding: SearchRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie, onMovieClick: (Movie) -> Unit) {
            binding.searchMovieTitle.text = movie.title ?: "Bilinmeyen Film"
            binding.searchMovieDetail.text = movie.description ?: "Açıklama bulunmuyor"
            binding.searchMovieRating.text = movie.rating?.toRatingString()

            loadMoviePoster(movie.poster)

            binding.root.setOnClickListener {
                onMovieClick(movie)
            }
        }

        private fun loadMoviePoster(posterPath: String?) {
            val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
            Glide.with(binding.root.context)
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(binding.searchMoviePoster)
        }

        fun setBottomMargin(isLastItem: Boolean) {
            val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            if (isLastItem) {
                layoutParams.bottomMargin = 60.dpToPx(binding.root.context)
            } else {
                layoutParams.bottomMargin = 0
            }
            binding.root.layoutParams = layoutParams
        }

        private fun Int.dpToPx(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieViewHolder {
        val binding = SearchRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchMovieViewHolder, position: Int) {
        val movie = movieList[position]

        holder.bind(movie, onMovieClick)

        holder.setBottomMargin(position == movieList.size - 1)
    }

    override fun getItemCount(): Int = movieList.size

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


