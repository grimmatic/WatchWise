package com.vakifbank.WatchWise.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.data.repository.FavoriteRepository
import com.vakifbank.WatchWise.data.repository.MoviesRepository
import com.vakifbank.WatchWise.data.repository.ReviewRepository
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieRatingSummary
import com.vakifbank.WatchWise.domain.model.MovieReview
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.ui.adapter.ReviewAdapter
import com.vakifbank.WatchWise.utils.parcelable
import com.vakifbank.WatchWise.utils.toRatingString
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailFragment : BaseFragment() {
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private var movieDetail: MovieDetail? = null
    private var trailerKey: String? = null
    private var isFavorite = false
    private lateinit var auth: FirebaseAuth
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewList = mutableListOf<MovieReview>()
    private var userReview: MovieReview? = null
    private var currentRating: Float = 0f
    private var currentComment: String = ""
    private var ratingSummary: MovieRatingSummary? = null

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

        setupReviewSection()
    }

    private fun setupReviewSection() {
        reviewAdapter = ReviewAdapter(reviewList, auth.currentUser?.uid)
        binding.reviewsRecyclerView.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }

        setupRatingButtons()

        binding.submitReviewButton.setOnClickListener {
            handleReviewSubmission()
        }

        binding.deleteReviewButton.setOnClickListener {
            handleReviewDeletion()
        }
    }

    private fun setupRatingButtons() {
        val ratingButtons = listOf(
            binding.rating1, binding.rating2, binding.rating3, binding.rating4, binding.rating5,
            binding.rating6, binding.rating7, binding.rating8, binding.rating9, binding.rating10
        )

        ratingButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                currentRating = (index + 1).toFloat()
                updateRatingButtons()
            }
        }
    }

    private fun updateRatingButtons() {
        val ratingButtons = listOf(
            binding.rating1, binding.rating2, binding.rating3, binding.rating4, binding.rating5,
            binding.rating6, binding.rating7, binding.rating8, binding.rating9, binding.rating10
        )

        ratingButtons.forEachIndexed { index, button ->
            if (index < currentRating.toInt()) {
                button.setColorFilter(requireContext().getColor(R.color.primary_purple))
            } else {
                button.setColorFilter(requireContext().getColor(R.color.gray))
            }
        }
    }

    private fun handleReviewSubmission() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Yorum yapmak için giriş yapmalısınız", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentRating == 0f) {
            Toast.makeText(requireContext(), "Lütfen bir puan verin", Toast.LENGTH_SHORT).show()
            return
        }

        val comment = binding.commentEditText.text.toString().trim()
        currentComment = comment

        val movieId = movieDetail?.id
        if (movieId == null) {
            Toast.makeText(requireContext(), "Film bilgisi bulunamadı", Toast.LENGTH_SHORT).show()
            return
        }

        binding.submitReviewButton.isEnabled = false
        binding.submitReviewButton.text = "Gönderiliyor..."

        lifecycleScope.launch {
            try {
                val success = if (userReview != null) {
                    ReviewRepository.updateReview(movieId, currentRating, comment)
                } else {
                    ReviewRepository.addReview(movieId, currentRating, comment)
                }

                if (success) {
                    Toast.makeText(requireContext(), "Yorumunuz kaydedildi", Toast.LENGTH_SHORT).show()
                    loadReviews()
                    loadRatingSummary()
                    binding.commentEditText.setText("")
                    currentRating = 0f
                    updateRatingButtons()
                } else {
                    Toast.makeText(requireContext(), "Yorum kaydedilemedi", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
            } finally {
                binding.submitReviewButton.isEnabled = true
                binding.submitReviewButton.text = "Gönder"
            }
        }
    }

    private fun handleReviewDeletion() {
        val movieId = movieDetail?.id ?: return

        binding.deleteReviewButton.isEnabled = false
        binding.deleteReviewButton.text = "Siliniyor..."

        lifecycleScope.launch {
            try {
                val success = ReviewRepository.deleteReview(movieId)
                if (success) {
                    Toast.makeText(requireContext(), "Yorumunuz silindi", Toast.LENGTH_SHORT).show()
                    loadReviews()
                    loadRatingSummary()
                    userReview = null
                    currentRating = 0f
                    currentComment = ""
                    binding.commentEditText.setText("")
                    updateRatingButtons()
                    updateReviewUI()
                } else {
                    Toast.makeText(requireContext(), "Yorum silinemedi", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
            } finally {
                binding.deleteReviewButton.isEnabled = true
                binding.deleteReviewButton.text = "Sil"
            }
        }
    }

    private fun loadReviews() {
        val movieId = movieDetail?.id ?: return

        lifecycleScope.launch {
            try {
                val reviews = ReviewRepository.getMovieReviews(movieId)
                reviewList.clear()
                reviewList.addAll(reviews)
                reviewAdapter.notifyDataSetChanged()

                userReview = ReviewRepository.getUserReview(movieId)
                updateReviewUI()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Yorumlar yüklenemedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRatingSummary() {
        val movieId = movieDetail?.id ?: return

        lifecycleScope.launch {
            try {
                ratingSummary = ReviewRepository.getMovieRatingSummary(movieId)
                updateRatingSummaryUI()
            } catch (e: Exception) {
            }
        }
    }

    private fun updateReviewUI() {
        if (auth.currentUser != null) {
            binding.reviewSection.visibility = View.VISIBLE

            userReview?.let { review ->
                binding.commentEditText.setText(review.comment)
                currentRating = review.rating ?: 0f
                currentComment = review.comment ?: ""
                updateRatingButtons()
                binding.deleteReviewButton.visibility = View.VISIBLE
            } ?: run {
                binding.deleteReviewButton.visibility = View.GONE
            }
        } else {
            binding.reviewSection.visibility = View.GONE
            binding.loginPromptTextView.visibility = View.VISIBLE
        }
    }

    private fun updateRatingSummaryUI() {
        ratingSummary?.let { summary ->
            binding.averageRatingTextView.text = summary.averageRating?.toRatingString() ?: "0.0"
            binding.totalReviewsTextView.text = "${summary.totalReviews ?: 0} değerlendirme"
            binding.ratingSummaryLayout.visibility = View.VISIBLE
        } ?: run {
            binding.ratingSummaryLayout.visibility = View.GONE
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
        loadReviews()
        loadRatingSummary()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}