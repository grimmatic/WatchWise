package com.vakifbank.WatchWise.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.data.repository.MoviesRepository
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.utils.parcelable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.net.toUri
import com.vakifbank.WatchWise.utils.toRatingString

class MovieDetailFragment : BaseFragment() {
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private var movieDetail: MovieDetail? = null
    private var trailerKey: String? = null

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

        binding.trailerButton.visibility = View.GONE
        binding.trailerButton.setOnClickListener {
            trailerKey?.let { key ->
                openYouTubeVideo(key)
            }
        }
    }

    private fun openYouTubeVideo(videoKey: String) {
        val youtubeIntent = Intent(Intent.ACTION_VIEW, "vnd.youtube:$videoKey".toUri())
        youtubeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val packageManager = requireActivity().packageManager
        val activities = packageManager.queryIntentActivities(youtubeIntent, 0)

        if (activities.isNotEmpty()) {
            startActivity(youtubeIntent)
        } else {
            // youtube yüklü değil tarayıcıda aç
            val youtubeUrl = "https://www.youtube.com/watch?v=$videoKey"
            val browserIntent = Intent(Intent.ACTION_VIEW, youtubeUrl.toUri())
            browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(browserIntent)
        }
    }

    private fun loadMovieTrailer(movieId: Int?) {
        movieId?.let { id ->
            val call = MoviesRepository.getMovieVideos(id)
            call.enqueue(object : Callback<MovieVideosResponse> {
                override fun onResponse(call: Call<MovieVideosResponse>, response: Response<MovieVideosResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.results?.let { videos ->
                            // official trailer
                            val trailer = videos.firstOrNull { video ->
                                video.site?.lowercase() == "youtube" &&
                                        video.type?.lowercase() == "trailer" &&
                                        video.official == true
                            } ?: videos.firstOrNull { video ->
                                // official yoksa herhangi
                                video.site?.lowercase() == "youtube" &&
                                        video.type?.lowercase() == "trailer"
                            }

                            trailer?.key?.let { key ->
                                trailerKey = key
                                binding.trailerButton.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MovieVideosResponse>, t: Throwable) {
                }
            })
        }
    }

    private fun setupMovieDetails() {
        binding.movieDescriptionTextView.movementMethod = android.text.method.ScrollingMovementMethod()

        binding.movieDescriptionTextView.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        movieDetail?.let { movie ->
            movie.title?.takeIf { it.isNotEmpty() }?.let { title ->
                binding.movieTitleTextView.text = title
                binding.movieTitleTextView.visibility = View.VISIBLE
            } ?: run {
                binding.movieTitleTextView.visibility = View.GONE
            }

            movie.description?.takeIf { it.isNotEmpty() }?.let { description ->
                binding.movieDescriptionTextView.text = description
                binding.movieDescriptionTextView.visibility = View.VISIBLE
            } ?: run {
                // Eğer türkçe description yoksa ingilizce için tekrar api çağrısı yap
                loadEnglishDescription(movie.id)
            }

            movie.language?.takeIf { it.isNotEmpty() }?.let { language ->
                binding.languageTextView.text = language
                binding.languageTextView.visibility = View.VISIBLE
            } ?: run {
                binding.languageTextView.visibility = View.GONE
            }

            movie.rating?.let { rating ->
                binding.ratingTextView.text = rating.toRatingString()
                binding.ratingTextView.visibility = View.VISIBLE
            } ?: run {
                binding.ratingTextView.visibility = View.GONE
            }

            movie.releaseDate?.takeIf { it.isNotEmpty() }?.let { releaseDate ->
                binding.releaseDateTextView.text = releaseDate.substringBefore("-")
                binding.releaseDateTextView.visibility = View.VISIBLE
            } ?: run {
                binding.releaseDateTextView.visibility = View.GONE
            }
            loadMoviePoster(posterPath = movie.poster)
            setupGenreViews(movie.genres)
            loadMovieTrailer( movieId = movie.id)
        }
    }

    private fun loadEnglishDescription(movieId: Int?) {
        movieId?.let { id ->
            val call = MoviesRepository.getMovieDetailsInEnglish(id)
            call.enqueue(object : Callback<MovieDetail> {
                override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                    if (response.isSuccessful) {
                        response.body()?.description?.takeIf { it.isNotEmpty() }?.let { englishDescription ->
                            binding.movieDescriptionTextView.text = englishDescription
                            binding.movieDescriptionTextView.visibility = View.VISIBLE
                        } ?: run {
                            binding.movieDescriptionTextView.visibility = View.GONE
                        }
                    } else {
                        binding.movieDescriptionTextView.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                    binding.movieDescriptionTextView.visibility = View.GONE
                }
            })
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
        binding.run {
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
        setupFavoriteButton(binding.favoriteFab)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}