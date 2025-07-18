package com.vakifbank.WatchWise.ui.fragment.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieRatingSummary
import com.vakifbank.WatchWise.domain.model.MovieReview
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.domain.usecase.AddReviewUseCase
import com.vakifbank.WatchWise.domain.usecase.AddToFavoritesUseCase
import com.vakifbank.WatchWise.domain.usecase.DeleteReviewUseCase
import com.vakifbank.WatchWise.domain.usecase.GetMovieDetailsInEnglishUseCase
import com.vakifbank.WatchWise.domain.usecase.GetMovieDetailsUseCase
import com.vakifbank.WatchWise.domain.usecase.GetMovieRatingSummaryUseCase
import com.vakifbank.WatchWise.domain.usecase.GetMovieReviewsUseCase
import com.vakifbank.WatchWise.domain.usecase.GetMovieVideosUseCase
import com.vakifbank.WatchWise.domain.usecase.GetUserReviewUseCase
import com.vakifbank.WatchWise.domain.usecase.IsMovieFavoriteUseCase
import com.vakifbank.WatchWise.domain.usecase.RemoveFromFavoritesUseCase
import com.vakifbank.WatchWise.domain.usecase.UpdateReviewUseCase
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
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val getMovieDetailsInEnglishUseCase: GetMovieDetailsInEnglishUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
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

    private val _englishDescription = MutableLiveData<String?>()
    val englishDescription: LiveData<String?> = _englishDescription

    private val _movieVideos = MutableLiveData<MovieVideosResponse?>()
    val movieVideos: LiveData<MovieVideosResponse?> = _movieVideos

    // livedata kullanımı
    private val _movieDetail = MutableLiveData<MovieDetail?>()
    val movieDetail: LiveData<MovieDetail?> = _movieDetail

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

    fun loadEnglishDescription(movieId: Int) {
        viewModelScope.launch {
            try {
                val movieDetail = getMovieDetailsInEnglishUseCase(movieId)
                _englishDescription.value = movieDetail.description
            } catch (e: Exception) {
                _englishDescription.value = ""
            }
        }
    }

    fun loadMovieVideos(movieId: Int) {
        viewModelScope.launch {
            try {
                val videos = getMovieVideosUseCase(movieId)
                _movieVideos.value = videos
            } catch (e: Exception) {
                _error.value = e.message
                _movieVideos.value = null
            }
        }
    }

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val detail = getMovieDetailsUseCase(movieId)
                _movieDetail.value = detail
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    fun loadMovieData(movieId: Int) {
        checkIfFavorite(movieId)
        loadUserReview(movieId)
        loadMovieReviews(movieId)
        loadRatingSummary(movieId)
        loadMovieVideos(movieId)
        loadMovieDetails(movieId)
    }
}