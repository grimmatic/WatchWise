package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.repository.ReviewRepository
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(movieId: Int): Boolean {
        return reviewRepository.deleteReview(movieId)
    }
}