package com.vakifbank.WatchWise.ui.fragment.movielist

import com.vakifbank.WatchWise.domain.usecase.GetUpcomingMoviesUseCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.usecase.GetPopularMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.GetTopRatedMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

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
        _isLoading.value = true
        getPopularMoviesUseCase(page).enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _popularMovies.value = response.body()?.movies ?: emptyList()
                } else {
                    _error.value = "Failed to load popular movies"
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
            }
        })
    }

    fun loadTopRatedMovies(page: Int = 1) {
        _isLoading.value = true
        getTopRatedMoviesUseCase(page).enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _topRatedMovies.value = response.body()?.movies ?: emptyList()
                } else {
                    _error.value = "Failed to load top rated movies"
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
            }
        })
    }

    fun loadUpcomingMovies(page: Int = 1) {
        _isLoading.value = true
        GetUpcomingMoviesUseCase(page).enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _upcomingMovies.value = response.body()?.movies ?: emptyList()
                } else {
                    _error.value = "Failed to load upcoming movies"
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
            }
        })
    }

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        searchMoviesUseCase(query).enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.movies ?: emptyList()
                } else {
                    _error.value = "Search failed"
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                _error.value = t.message
            }
        })
    }

    fun loadAllMovies() {
        loadPopularMovies()
        loadTopRatedMovies()
        loadUpcomingMovies()
    }
}