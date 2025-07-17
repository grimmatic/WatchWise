package com.vakifbank.WatchWise.domain.repository

import com.vakifbank.WatchWise.domain.model.MovieRatingSummary
import com.vakifbank.WatchWise.domain.model.MovieReview

interface ReviewRepository {
    suspend fun addReview(movieId: Int, rating: Float, comment: String): Boolean
    suspend fun updateReview(movieId: Int, rating: Float, comment: String): Boolean
    suspend fun getUserReview(movieId: Int): MovieReview?
    suspend fun getMovieReviews(movieId: Int): List<MovieReview>
    suspend fun getMovieRatingSummary(movieId: Int): MovieRatingSummary?
    suspend fun deleteReview(movieId: Int): Boolean
}