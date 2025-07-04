package com.vakifbank.WatchWise.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vakifbank.WatchWise.domain.model.Movie

class MovieAdapter (private val movieList: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val filmImage = itemView.findViewById<View>(com.vakifbank.WatchWise.R.id.filmImage)
        val filmTitle = itemView.findViewById<View>(com.vakifbank.WatchWise.R.id.filmTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {

        return
    }

    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}