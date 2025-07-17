package com.vakifbank.WatchWise.ui.fragment.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieRatingSummary
import com.vakifbank.WatchWise.domain.model.MovieReview
import com.vakifbank.WatchWise.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val isMovieFavoriteUseCase: IsMovieFavoriteUseCase,
    private val addReviewUseCase: AddReviewUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val getUserReviewUseCase: GetUserReviewUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val getMovieRatingSummaryUseCase: GetMovieRatingSummaryUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
) : ViewModel() {

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _userReview = MutableLiveData<MovieReview?>()
    val userReview: LiveData<MovieReview?> = _userReview

    private val _movieReviews = MutableLiveData<List<MovieReview>>()
    val movieReviews: LiveData<List<MovieReview>> = _movieReviews

    private val _ratingSummary = MutableLiveData<MovieRatingSummary?>()
    val ratingSummary: LiveData<MovieRatingSummary?> = _ratingSummary

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _favoriteResult = MutableLiveData<Boolean>()
    val favoriteResult: LiveData<Boolean> = _favoriteResult

    private val _reviewResult = MutableLiveData<Boolean>()
    val reviewResult: LiveData<Boolean> = _reviewResult

    fun checkIfFavorite(movieId: Int) {
        viewModelScope.launch {
            try {
                val result = isMovieFavoriteUseCase(movieId)
                _isFavorite.value = result
            } catch (e: Exception) {
                _isFavorite.value = false
                _error.value = e.message
            }
        }
    }

    fun addToFavorites(movie: Movie) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = addToFavoritesUseCase(movie)
                _favoriteResult.value = result
                if (result) {
                    _isFavorite.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message
                _favoriteResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = removeFromFavoritesUseCase(movieId)
                _favoriteResult.value = result
                if (result) {
                    _isFavorite.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _favoriteResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserReview(movieId: Int) {
        viewModelScope.launch {
            try {
                val review = getUserReviewUseCase(movieId)
                _userReview.value = review
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadMovieReviews(movieId: Int) {
        viewModelScope.launch {
            try {
                val reviews = getMovieReviewsUseCase(movieId)
                _movieReviews.value = reviews
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun loadRatingSummary(movieId: Int) {
        viewModelScope.launch {
            try {
                val summary = getMovieRatingSummaryUseCase(movieId)
                _ratingSummary.value = summary
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addReview(movieId: Int, rating: Float, comment: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = addReviewUseCase(movieId, rating, comment)
                _reviewResult.value = result
                if (result) {
                    loadUserReview(movieId)
                    loadMovieReviews(movieId)
                    loadRatingSummary(movieId)
                }
            } catch (e: Exception) {
                _error.value = e.message
                _reviewResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReview(movieId: Int, rating: Float, comment: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = updateReviewUseCase(movieId, rating, comment)
                _reviewResult.value = result
                if (result) {
                    loadUserReview(movieId)
                    loadMovieReviews(movieId)
                    loadRatingSummary(movieId)
                }
            } catch (e: Exception) {
                _error.value = e.message
                _reviewResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReview(movieId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = deleteReviewUseCase(movieId)
                _reviewResult.value = result
                if (result) {
                    _userReview.value = null
                    loadMovieReviews(movieId)
                    loadRatingSummary(movieId)
                }
            } catch (e: Exception) {
                _error.value = e.message
                _reviewResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMovieData(movieId: Int) {
        checkIfFavorite(movieId)
        loadUserReview(movieId)
        loadMovieReviews(movieId)
        loadRatingSummary(movieId)
    }
}