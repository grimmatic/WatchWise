package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveFromFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(movieId: Int): Boolean {
        return favoriteRepository.removeFromFavorites(movieId)
    }
}