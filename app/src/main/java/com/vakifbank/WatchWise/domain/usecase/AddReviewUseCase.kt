package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.repository.ReviewRepository
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(movieId: Int, rating: Float, comment: String): Boolean {
        return reviewRepository.addReview(movieId, rating, comment)
    }
}