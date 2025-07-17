package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.repository.ReviewRepository
import javax.inject.Inject

class GetMovieRatingSummaryUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(movieId: Int) = reviewRepository.getMovieRatingSummary(movieId)
}

