package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.domain.model.Movie

class MovieDetailFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailBinding
    private var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let { bundle ->
            movie = bundle.getParcelable("movie_data")
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMovieDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.movieTitleTextView.setText(movie?.title)
       /* val movieTitle = arguments?.getString("movieTitle") ?: ""
        val moviePoster = arguments?.getString("moviePoster") ?: ""
        val movieDescription = arguments?.getString("movieDescription") ?: ""
        val movieRating = arguments?.getFloat("movieRating") ?: 0.0f
        val movieYear = arguments?.getString("movieYear") ?: ""
        val movieLanguage = arguments?.getString("movieLanguage") ?: ""
        val movieGenres = arguments?.getString("movieGenres") ?: ""*/


    }


}