package com.vakifbank.WatchWise.ui.fragment.moviedetail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.base.BaseFragment
import com.vakifbank.WatchWise.databinding.FragmentMovieDetailBinding
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.ui.adapter.ReviewAdapter
import com.vakifbank.WatchWise.utils.parcelable
import com.vakifbank.WatchWise.utils.toRatingString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailFragment : BaseFragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels()

    private var trailerKey: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewList = mutableListOf<com.vakifbank.WatchWise.domain.model.MovieReview>()
    private var currentRating: Float = 0f
    private var currentComment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        arguments?.let { bundle ->
            val movieDetail = bundle.parcelable<MovieDetail>("movie_detail_data")
            movieDetail?.let {
                viewModel.setMovieDetail(it)
            }
        }
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
        initUI()
        setupObservers()

        viewModel.movieDetail.observe(viewLifecycleOwner) { movieDetail ->
            movieDetail?.let {
                setupMovieDetailsUI(it)
                it.id?.let { movieId ->
                    viewModel.loadMovieData(movieId)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        viewModel.userReview.observe(viewLifecycleOwner) { review ->
            review?.let {
                binding.commentEditText.setText(it.comment)
                currentRating = it.rating ?: 0f
                currentComment = it.comment ?: ""
                updateRatingButtons()
                binding.deleteReviewButton.visibility = View.VISIBLE
            } ?: run {
                binding.deleteReviewButton.visibility = View.GONE
            }
        }

        viewModel.movieReviews.observe(viewLifecycleOwner) { reviews ->
            reviewList.clear()
            reviewList.addAll(reviews)
            reviewAdapter.notifyDataSetChanged()
        }

        viewModel.ratingSummary.observe(viewLifecycleOwner) { summary ->
            summary?.let {
                binding.averageRatingTextView.text = it.averageRating?.toRatingString() ?: "0.0"
                binding.totalReviewsTextView.text = "${it.totalReviews ?: 0} değerlendirme"
                binding.ratingSummaryLayout.visibility = View.VISIBLE
            } ?: run {
                binding.ratingSummaryLayout.visibility = View.GONE
            }
        }

        viewModel.englishDescription.observe(viewLifecycleOwner) { description ->
            if (!description.isNullOrEmpty()) {
                viewModel.movieDetail.value?.let { movieDetail ->
                    if (movieDetail.description.isNullOrEmpty()) {
                        binding.movieDescriptionTextView.text = description
                        binding.movieDescriptionTextView.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.submitReviewButton.isEnabled = !isLoading
            binding.deleteReviewButton.isEnabled = !isLoading

            if (isLoading) {
                binding.submitReviewButton.text = "Gönderiliyor..."
                binding.deleteReviewButton.text = "Siliniyor..."
            } else {
                binding.submitReviewButton.text = "Gönder"
                binding.deleteReviewButton.text = "Sil"
            }
        }

        viewModel.favoriteResult.observe(viewLifecycleOwner) { success ->
            val message = if (success) {
                if (viewModel.isFavorite.value == true) "Favorilere eklendi" else "Favorilerden çıkarıldı"
            } else {
                "İşlem başarısız"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.reviewResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "İşlem başarılı", Toast.LENGTH_SHORT).show()
                binding.commentEditText.setText("")
                currentRating = 0f
                updateRatingButtons()
            } else {
                Toast.makeText(requireContext(), "İşlem başarısız", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.movieVideos.observe(viewLifecycleOwner) { videosResponse ->
            videosResponse?.results?.find { video ->
                video.type == "Trailer" && video.site == "YouTube"
            }?.let { trailer ->
                trailerKey = trailer.key
                binding.trailerButton.visibility = View.VISIBLE
            } ?: run {
                binding.trailerButton.visibility = View.GONE
            }
        }
    }

    private fun initUI() {
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
                Toast.makeText(
                    requireContext(),
                    "Favorileri görmek için giriş yapmalısınız",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        setupReviewSection()
        updateGoToFavoritesButton()
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

        updateReviewUI()
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
            Toast.makeText(
                requireContext(),
                "Yorum yapmak için giriş yapmalısınız",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (currentRating == 0f) {
            Toast.makeText(requireContext(), "Lütfen bir puan verin", Toast.LENGTH_SHORT).show()
            return
        }

        val comment = binding.commentEditText.text.toString().trim()
        currentComment = comment

        val movieId = viewModel.movieDetail.value?.id
        if (movieId == null) {
            Toast.makeText(requireContext(), "Film bilgisi bulunamadı", Toast.LENGTH_SHORT).show()
            return
        }

        if (viewModel.userReview.value != null) {
            viewModel.updateReview(movieId, currentRating, comment)
        } else {
            viewModel.addReview(movieId, currentRating, comment)
        }
    }

    private fun handleReviewDeletion() {
        val movieId = viewModel.movieDetail.value?.id ?: return
        viewModel.deleteReview(movieId)
    }

    private fun updateReviewUI() {
        if (auth.currentUser != null) {
            binding.reviewSection.visibility = View.VISIBLE
            binding.loginPromptTextView.visibility = View.GONE
        } else {
            binding.reviewSection.visibility = View.GONE
            binding.loginPromptTextView.visibility = View.VISIBLE
        }
    }

    private fun handleFavoriteClick() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(
                requireContext(),
                "Favorilere eklemek için giriş yapmalısınız",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.movieDetail.value?.let { detail ->
            val isFavorite = viewModel.isFavorite.value ?: false

            if (isFavorite) {
                detail.id?.let { movieId ->
                    viewModel.removeFromFavorites(movieId)
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
                viewModel.addToFavorites(movie)
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
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
            val youtubeUrl = "https://www.youtube.com/watch?v=$videoKey"
            val browserIntent = Intent(Intent.ACTION_VIEW, youtubeUrl.toUri())
            browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(browserIntent)
        }
    }

    private fun setupMovieDetailsUI(movieDetail: MovieDetail) {
        binding.movieDescriptionTextView.movementMethod =
            android.text.method.ScrollingMovementMethod()

        binding.movieDescriptionTextView.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        movieDetail.title?.takeIf { it.isNotEmpty() }?.let { title ->
            binding.movieTitleTextView.text = title
            binding.movieTitleTextView.visibility = View.VISIBLE
        } ?: run {
            binding.movieTitleTextView.visibility = View.GONE
        }

        movieDetail.description?.takeIf { it.isNotEmpty() }?.let { description ->
            binding.movieDescriptionTextView.text = description
            binding.movieDescriptionTextView.visibility = View.VISIBLE
        } ?: run {
            movieDetail.id?.let { movieId ->
                viewModel.loadEnglishDescription(movieId)
            }
        }

        movieDetail.language?.takeIf { it.isNotEmpty() }?.let { language ->
            binding.languageTextView.text = language
            binding.languageTextView.visibility = View.VISIBLE
        } ?: run {
            binding.languageTextView.visibility = View.GONE
        }

        movieDetail.rating?.let { rating ->
            binding.ratingTextView.text = rating.toRatingString()
            binding.ratingTextView.visibility = View.VISIBLE
        } ?: run {
            binding.ratingTextView.visibility = View.GONE
        }

        movieDetail.releaseDate?.takeIf { it.isNotEmpty() }?.let { releaseDate ->
            binding.releaseDateTextView.text = releaseDate.substringBefore("-")
            binding.releaseDateTextView.visibility = View.VISIBLE
        } ?: run {
            binding.releaseDateTextView.visibility = View.GONE
        }

        loadMoviePoster(posterPath = movieDetail.poster)
        setupGenreViews(movieDetail.genres)
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
                    genreTextView1.text = genreList[0].name
                    genreLayoutVisible(genreLinear1)
                    genreLayoutGone(genreLinear2)
                    genreLayoutGone(genreLinear3)
                }

                2 -> {
                    genreTextView1.text = genreList[0].name
                    genreTextView2.text = genreList[1].name
                    genreLayoutVisible(genreLinear1)
                    genreLayoutVisible(genreLinear2)
                    genreLayoutGone(genreLinear3)
                }

                else -> {
                    genreTextView1.text = genreList[0].name
                    genreTextView2.text = genreList[1].name
                    genreTextView3.text = genreList[2].name
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
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}