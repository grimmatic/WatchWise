package com.vakifbank.WatchWise.ui.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
//import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.domain.model.Movie

enum class MovieListType {
                         //List tipine göre color değiştirmek için kullanıyoruz
    POPULAR, TOP_RATED, UPCOMING
}

class MovieAdapter(
    private val movieList: List<Movie>,
    private val listType: MovieListType = MovieListType.POPULAR,
    private val onMovieClick: (Movie) -> Unit = {}
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // private lateinit var rowType: LinearLayout

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filmImage: ImageView = itemView.findViewById(R.id.filmImage)
        val filmTitle: TextView = itemView.findViewById(R.id.filmTitle)
        val containerLayout: LinearLayout = itemView.findViewById(R.id.containerLayout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_row, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.filmTitle.text = movie.title


        when (listType) {
            MovieListType.POPULAR -> holder.containerLayout.setBackgroundResource(R.drawable.rounded_bg_popular)
            MovieListType.TOP_RATED -> holder.containerLayout.setBackgroundResource(R.drawable.rounded_bg_top_rated)
            MovieListType.UPCOMING -> holder.containerLayout.setBackgroundResource(R.drawable.rounded_bg_upcoming)
        }


        // Poster stringini görsel (Int)'e çevirmek için Glide kullandık
        val posterUrl = "https://image.tmdb.org/t/p/w500${movie.poster}"
        Glide.with(holder.itemView.context)
            .load(posterUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.filmImage)


        holder.itemView.setOnClickListener {
            onMovieClick(movie)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}