package com.vakifbank.WatchWise.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.data.repository.FavoriteRepository
import com.vakifbank.WatchWise.data.repository.MoviesRepository
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.utils.parcelable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.net.toUri
import com.vakifbank.WatchWise.utils.toRatingString
import kotlinx.coroutines.launch

class MovieDetailFragment : BaseFragment() {
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private var movieDetail: MovieDetail? = null
    private var trailerKey: String? = null
    private var isFavorite = false
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
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

        binding.addToFavoriteButton.setOnClickListener {
            handleFavoriteClick()
        }

        binding.goToFavoritesFab.setOnClickListener {
            if (auth.currentUser != null) {
                findNavController().navigate(R.id.action_movieDetailFragment_to_favoriteFragment)
            } else {
                Toast.makeText(requireContext(), "Favorileri görmek için giriş yapmalısınız", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleFavoriteClick() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Favorilere eklemek için giriş yapmalısınız", Toast.LENGTH_SHORT).show()
            return
        }

        movieDetail?.let { detail ->
            lifecycleScope.launch {
                try {
                    if (isFavorite) {
                        detail.id?.let { movieId ->
                            val success = FavoriteRepository.removeFromFavorites(movieId)
                            if (success) {
                                isFavorite = false
                                updateFavoriteButton()
                                Toast.makeText(requireContext(), "Favorilerden çıkarıldı", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Favorilerden çıkarılırken hata oluştu", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val movie = Movie(
                            id = detail.id,
                            title = detail.title,
                            poster = detail.poster,
                            description = detail.description,
                            rating = detail.rating,
                            tagline = detail.tagline
                        )

                        val success = FavoriteRepository.addToFavorites(movie)
                        if (success) {
                            isFavorite = true
                            updateFavoriteButton()
                            Toast.makeText(requireContext(), "Favorilere eklendi", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Favorilere eklenirken hata oluştu", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkIfFavorite() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            movieDetail?.id?.let { movieId ->
                lifecycleScope.launch {
                    try {
                        isFavorite = FavoriteRepository.isMovieFavorite(movieId)
                        updateFavoriteButton()
                        updateGoToFavoritesButton()
                    } catch (e: Exception) {
                        isFavorite = false
                        updateFavoriteButton()
                        updateGoToFavoritesButton()
                    }
                }
            }
        } else {
            updateGoToFavoritesButton()
        }
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            binding.favoriteButtonIcon.setImageResource(android.R.drawable.btn_star_big_on)
            binding.favoriteButtonText.text = "Favorilerden Çıkar"
            binding.favoriteButtonSubtext.text = "Bu filmi favorilerinizden çıkarın"
            binding.addToFavoriteButton.setCardBackgroundColor(requireContext().getColor(R.color.top_rated_green))
            binding.addFavoriteLinear.setBackgroundColor(requireContext().getColor(R.color.top_rated_green))
        } else {
            binding.favoriteButtonIcon.setImageResource(android.R.drawable.btn_star_big_off)
            binding.favoriteButtonText.text = "Favorilere Ekle"
            binding.favoriteButtonSubtext.text = "Bu filmi favorilerinize ekleyin"
            binding.addToFavoriteButton.setCardBackgroundColor(requireContext().getColor(R.color.primary_purple))
            binding.addFavoriteLinear.setBackgroundColor(requireContext().getColor(R.color.primary_purple))
        }
    }

    private fun updateGoToFavoritesButton() {
        if (auth.currentUser != null) {
            binding.goToFavoritesFab.visibility = View.VISIBLE
        } else {
            binding.goToFavoritesFab.visibility = View.GONE
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
            loadMovieTrailer(movieId = movie.id)
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

    private fun genreLayoutGone(genreLayout: LinearLayout) {
        genreLayout.visibility = View.GONE
    }

    private fun genreLayoutVisible(genreLayout: LinearLayout) {
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
        checkIfFavorite()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}