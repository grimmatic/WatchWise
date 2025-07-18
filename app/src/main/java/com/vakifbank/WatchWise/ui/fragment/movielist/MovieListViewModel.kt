package com.vakifbank.WatchWise.ui.fragment.movielist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vakifbank.WatchWise.base.BaseViewModel
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.usecase.GetMovieDetailsUseCase
import com.vakifbank.WatchWise.domain.usecase.GetPopularMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.GetTopRatedMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.GetUpcomingMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : BaseViewModel() {


    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies: LiveData<List<Movie>> = _topRatedMovies

    private val _upcomingMovies = MutableLiveData<List<Movie>>()
    val upcomingMovies: LiveData<List<Movie>> = _upcomingMovies

    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadPopularMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = getPopularMoviesUseCase(page)
                _popularMovies.value = response.movies ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load popular movies"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTopRatedMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = getTopRatedMoviesUseCase(page)
                _topRatedMovies.value = response.movies ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load top rated movies"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUpcomingMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = getUpcomingMoviesUseCase(page)
                _upcomingMovies.value = response.movies ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load upcoming movies"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val response = searchMoviesUseCase(query)
                _searchResults.value = response.movies ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message ?: "Search failed"
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

    fun loadAllMovies() {
        loadPopularMovies()
        loadTopRatedMovies()
        loadUpcomingMovies()
    }
}