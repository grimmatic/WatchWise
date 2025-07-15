package com.vakifbank.WatchWise.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vakifbank.WatchWise.domain.model.MovieRatingSummary
import com.vakifbank.WatchWise.domain.model.MovieReview
import kotlinx.coroutines.tasks.await

object ReviewRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun getMovieReviewsCollection(movieId: Int) =
        firestore.collection("movie_reviews")
            .document(movieId.toString())
            .collection("reviews")

    private fun getMovieRatingSummaryDocument(movieId: Int) =
        firestore.collection("movie_ratings")
            .document(movieId.toString())

    suspend fun addReview(movieId: Int, rating: Float, comment: String): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val reviewId = "${currentUser.uid}_${movieId}"
                val timestamp = System.currentTimeMillis()

                val review = MovieReview(
                    reviewId = reviewId,
                    movieId = movieId,
                    userId = currentUser.uid,
                    userEmail = currentUser.email,
                    rating = rating,
                    comment = comment,
                    timestamp = timestamp
                )

                val reviewData = hashMapOf(
                    "reviewId" to review.reviewId,
                    "movieId" to review.movieId,
                    "userId" to review.userId,
                    "userEmail" to review.userEmail,
                    "rating" to review.rating,
                    "comment" to review.comment,
                    "timestamp" to review.timestamp
                )

                getMovieReviewsCollection(movieId)
                    .document(reviewId)
                    .set(reviewData)
                    .await()

                updateRatingSummary(movieId)
                true
            } else
                false

        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateReview(movieId: Int, rating: Float, comment: String): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val reviewId = "${currentUser.uid}_${movieId}"
                val timestamp = System.currentTimeMillis()

                val updateData = hashMapOf(
                    "rating" to rating,
                    "comment" to comment,
                    "timestamp" to timestamp
                )

                getMovieReviewsCollection(movieId)
                    .document(reviewId)
                    .update(updateData as Map<String, Any>)
                    .await()

                updateRatingSummary(movieId)
                true
            } else
                false

        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserReview(movieId: Int): MovieReview? {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val reviewId = "${currentUser.uid}_${movieId}"
                val document = getMovieReviewsCollection(movieId)
                    .document(reviewId)
                    .get()
                    .await()

                if (document.exists()) {
                    MovieReview(
                        reviewId = document.getString("reviewId"),
                        movieId = document.getLong("movieId")?.toInt(),
                        userId = document.getString("userId"),
                        userEmail = document.getString("userEmail"),
                        rating = document.getDouble("rating")?.toFloat(),
                        comment = document.getString("comment"),
                        timestamp = document.getLong("timestamp")
                    )
                } else
                    null

            } else
                null

        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieReviews(movieId: Int): List<MovieReview> {
        return try {
            val querySnapshot = getMovieReviewsCollection(movieId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                try {
                    MovieReview(
                        reviewId = document.getString("reviewId"),
                        movieId = document.getLong("movieId")?.toInt(),
                        userId = document.getString("userId"),
                        userEmail = document.getString("userEmail"),
                        rating = document.getDouble("rating")?.toFloat(),
                        comment = document.getString("comment"),
                        timestamp = document.getLong("timestamp")
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMovieRatingSummary(movieId: Int): MovieRatingSummary? {
        return try {
            val document = getMovieRatingSummaryDocument(movieId)
                .get()
                .await()

            if (document.exists()) {
                MovieRatingSummary(
                    movieId = document.getLong("movieId")?.toInt(),
                    averageRating = document.getDouble("averageRating")?.toFloat(),
                    totalReviews = document.getLong("totalReviews")?.toInt(),
                    ratingDistribution = document.get("ratingDistribution") as? Map<Int, Int>
                )
            } else
                null

        } catch (e: Exception) {
            null
        }
    }

    private suspend fun updateRatingSummary(movieId: Int) {
        try {
            val reviews = getMovieReviews(movieId)

            if (reviews.isNotEmpty()) {
                val ratings = reviews.mapNotNull { it.rating }
                val averageRating = ratings.average().toFloat()
                val totalReviews = reviews.size

                val ratingDistribution = mutableMapOf<Int, Int>()
                for (i in 1..10) {
                    ratingDistribution[i] = 0
                }

                ratings.forEach { rating ->
                    val roundedRating = rating.toInt().coerceIn(1, 10)
                    ratingDistribution[roundedRating] = ratingDistribution[roundedRating]!! + 1
                }

                val summaryData = hashMapOf(
                    "movieId" to movieId,
                    "averageRating" to averageRating,
                    "totalReviews" to totalReviews,
                    "ratingDistribution" to ratingDistribution
                )

                getMovieRatingSummaryDocument(movieId)
                    .set(summaryData)
                    .await()
            }
        } catch (e: Exception) {
        }
    }

    suspend fun deleteReview(movieId: Int): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val reviewId = "${currentUser.uid}_${movieId}"

                getMovieReviewsCollection(movieId)
                    .document(reviewId)
                    .delete()
                    .await()

                updateRatingSummary(movieId)
                true
            } else
                false

        } catch (e: Exception) {
            false
        }
    }
}