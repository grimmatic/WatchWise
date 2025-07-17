package com.vakifbank.WatchWise.ui.fragment.seemore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.usecase.GetPopularMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.GetTopRatedMoviesUseCase
import com.vakifbank.WatchWise.domain.usecase.GetUpcomingMoviesUseCase
import com.vakifbank.WatchWise.ui.fragment.MovieCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SeeMoreViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase
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

        _isLoading.value = true
        currentPage = page

        val call = when(categoryType) {
            MovieCategory.POPULAR -> getPopularMoviesUseCase(page)
            MovieCategory.TOP_RATED -> getTopRatedMoviesUseCase(page)
            MovieCategory.UPCOMING -> getUpcomingMoviesUseCase(page)
        }

        call.enqueue(object : Callback<GetMoviesResponse> {
            override fun onResponse(call: Call<GetMoviesResponse>, response: Response<GetMoviesResponse>) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    val moviesResponse = response.body()
                    moviesResponse?.let {
                        totalPages = it.totalPages ?: 1
                        it.movies?.let { newMovies ->
                            if (page == 1) {
                                currentMovieList.clear()
                            }
                            currentMovieList.addAll(newMovies)
                            _movies.value = currentMovieList.toList()

                            _hasMorePages.value = currentPage < totalPages
                        }
                    }
                } else {
                    _error.value = "Filmler yüklenirken hata oluştu"
                }
            }

            override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message ?: "Bilinmeyen hata"
            }
        })
    }

    fun loadNextPage() {
        if (currentPage < totalPages && _isLoading.value != true) {
            loadMovies(currentPage + 1)
        }
    }
}