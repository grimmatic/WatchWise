package com.vakifbank.WatchWise.domain.repository

import com.vakifbank.WatchWise.domain.model.Movie

interface FavoriteRepository {
    suspend fun addToFavorites(movie: Movie): Boolean
    suspend fun removeFromFavorites(movieId: Int): Boolean
    suspend fun getFavoriteMovies(): List<Movie>
    suspend fun isMovieFavorite(movieId: Int): Boolean
}