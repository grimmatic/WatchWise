package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.model.Movie
import com.vakifbank.WatchWise.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(): List<Movie> {
        return favoriteRepository.getFavoriteMovies()
    }
}