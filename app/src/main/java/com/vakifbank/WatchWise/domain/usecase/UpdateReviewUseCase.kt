package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.repository.ReviewRepository
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(movieId: Int, rating: Float, comment: String): Boolean {
        return reviewRepository.updateReview(movieId, rating, comment)
    }
}
