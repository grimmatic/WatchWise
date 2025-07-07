package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.utils.parcelable

class MovieDetailFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailBinding
    private var movie: Movie? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let { bundle ->
            movie=bundle.parcelable<Movie>("movie_data")
        }
        //Detay Servisi eklenicek liste fragmentta sadece poster ve title olacak detay da hepsini tekrar çekicez
        initUi()
        deneme()

        super.onCreate(savedInstanceState)
    }

    private fun initUi() {
        binding?.run {

        }
    }

    private fun deneme() {
        binding.run {
            binding.movieTitleTextView.setText(movie?.title)
            binding.movieDescriptionTextView.setText(movie?.description)
        }
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
        binding.movieDescriptionTextView.setText(movie?.description)
        //binding.movieRatingTextView.setText(movie?.rating.toString())
        //binding.movieYearTextView.setText(movie?.year)
        //binding.movieLanguageTextView.setText(movie?.language)
    }


}