package com.vakifbank.WatchWise.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieReview(
    val reviewId: String? = null,
    val movieId: Int? = null,
    val userId: String? = null,
    val userEmail: String? = null,
    val rating: Float? = null,
    val comment: String? = null,
    val timestamp: Long? = null
) : Parcelable

@Parcelize
data class MovieRatingSummary(
    val movieId: Int? = null,
    val averageRating: Float? = null,
    val totalReviews: Int? = null,
    val ratingDistribution: Map<Int, Int>? = null
) : Parcelable