package com.vakifbank.WatchWise.ui.fragment.seemore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.usecase.GetMovieDetailsUseCase
import com.vakifbank.WatchWise.domain.usecase.GetPopularMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.GetTopRatedMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeMoreViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _hasMorePages = MutableLiveData<Boolean>()
    val hasMorePages: LiveData<Boolean> = _hasMorePages

    private var currentPage = 1
    private var totalPages = 1
    private var categoryType: MovieCategory = MovieCategory.POPULAR
    private val currentMovieList = mutableListOf<Movie>()

    fun setCategoryType(category: MovieCategory) {
        categoryType = category
        currentPage = 1
        currentMovieList.clear()
        _hasMorePages.value = true
    }

    fun loadMovies(page: Int) {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                currentPage = page

                val response = when (categoryType) {
                    MovieCategory.POPULAR -> getPopularMoviesUseCase(page)
                    MovieCategory.TOP_RATED -> getTopRatedMoviesUseCase(page)
                    MovieCategory.UPCOMING -> getUpcomingMoviesUseCase(page)
                }

                totalPages = response.totalPages ?: 1
                response.movies?.let { newMovies ->
                    if (page == 1) {
                        currentMovieList.clear()
                    }
                    currentMovieList.addAll(newMovies)
                    _movies.value = currentMovieList.toList()
                    _hasMorePages.value = currentPage < totalPages
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Bilinmeyen hata"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMovieDetailsForNavigation(movieId: Int?, onSuccess: (MovieDetail) -> Unit) {
        viewModelScope.launch {
            try {
                val movieDetail = getMovieDetailsUseCase(movieId)
                onSuccess(movieDetail)
            } catch (e: Exception) {
                _error.value = "Film detayları yüklenemedi"
            }
        }
    }

    fun loadNextPage() {
        if (currentPage < totalPages && _isLoading.value != true) {
            loadMovies(currentPage + 1)
        }
    }
}