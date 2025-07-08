package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.utils.parcelable

class MovieDetailFragment : Fragment() {
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private var movieDetail: MovieDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            movieDetail = bundle.parcelable<MovieDetail>("movie_detail_data")
        }
    }

    private fun initUi() {
        binding.floatingActionButton.setOnClickListener {
            navigateBack()
        }
    }

    private fun setupMovieDetails() {
        movieDetail?.let { movie ->
            binding.movieTitleTextView.text = movie.title ?: "Başlık bulunamadı"
            binding.movieDescriptionTextView.text = movie.description ?: "Açıklama bulunamadı"
            loadMoviePoster(movie.poster)
            setupGenreViews(movie.genres)
        }
    }
    private fun loadMoviePoster(posterPath: String?) {
        val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
        Glide.with(requireContext())
            .load(posterUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.posterImageView)
    }
    private fun setupGenreViews(genres: List<com.vakifbank.WatchWise.domain.model.Genre>?) {
        binding?.run {
            val genreList = genres?.take(3) ?: emptyList()

            when (genreList.size) {
                0 -> {
                    genreLayoutGone(genreLinear1)
                    genreLayoutGone(genreLinear2)
                    genreLayoutGone(genreLinear3)
                }
                1 -> {
                    genreTextView1.apply { text = genreList[0].name }
                    genreLayoutVisible(genreLinear1)
                    genreLayoutGone(genreLinear2)
                    genreLayoutGone(genreLinear3)
                }
                2 -> {
                    genreTextView1.apply { text = genreList[0].name }
                    genreTextView2.apply { text = genreList[1].name }
                    genreLayoutVisible(genreLinear1)
                    genreLayoutVisible(genreLinear2)
                    genreLayoutGone(genreLinear3)
                }
                else -> {
                    genreTextView1.apply { text = genreList[0].name }
                    genreTextView2.apply { text = genreList[1].name }
                    genreTextView3.apply { text = genreList[2].name }
                    genreLayoutVisible(genreLinear1)
                    genreLayoutVisible(genreLinear2)
                    genreLayoutVisible(genreLinear3)
                }
            }
        }
    }

    fun genreLayoutGone(genreLayout: LinearLayout){
        genreLayout.visibility = View.GONE
    }
    fun genreLayoutVisible(genreLayout: LinearLayout){
        genreLayout.visibility = View.VISIBLE
    }
    private fun navigateBack() {
        findNavController().navigateUp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setupMovieDetails()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}