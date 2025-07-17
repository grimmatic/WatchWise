package com.vakifbank.WatchWise.ui.fragment.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.usecase.GetFavoriteMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
) : ViewModel() {

    private val _favoriteMovies = MutableLiveData<List<Movie>>()
    val favoriteMovies: LiveData<List<Movie>> = _favoriteMovies

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    fun loadFavoriteMovies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val movies = getFavoriteMoviesUseCase()
                _favoriteMovies.value = movies
                _isEmpty.value = movies.isEmpty()
            } catch (e: Exception) {
                _error.value = e.message
                _favoriteMovies.value = emptyList()
                _isEmpty.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
}