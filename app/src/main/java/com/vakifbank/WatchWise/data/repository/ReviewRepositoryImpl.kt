package com.vakifbank.WatchWise.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vakifbank.WatchWise.domain.model.MovieRatingSummary
import com.vakifbank.WatchWise.domain.model.MovieReview
import com.vakifbank.WatchWise.domain.repository.ReviewRepository
import com.vakifbank.WatchWise.utils.Constant
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    private val REVIEW_ID: String = Constant.ReviewConstants.REVIEW_ID
    private val REVIEWS: String = Constant.ReviewConstants.REVIEWS
    private val RATING: String = Constant.ReviewConstants.RATING
    private val COMMENT: String = Constant.ReviewConstants.COMMENT
    private val TIMESTAMP: String = Constant.ReviewConstants.TIMESTAMP
    private val MOVIE_ID: String = Constant.ReviewConstants.MOVIE_ID
    private val USER_ID: String = Constant.ReviewConstants.USER_ID
    private val USER_EMAIL: String = Constant.ReviewConstants.USER_EMAIL
    private val AVERAGE_RATING: String = Constant.ReviewConstants.AVERAGE_RATING
    private val TOTAL_REVIEWS: String = Constant.ReviewConstants.TOTAL_REVIEWS
    private val RATING_DISTRIBUTION: String = Constant.ReviewConstants.RATING_DISTRIBUTION

    private fun getMovieReviewsCollection(movieId: Int): CollectionReference =
        firestore.collection("movie_reviews")
            .document(movieId.toString())
            .collection(REVIEWS)

    private fun getMovieRatingSummaryDocument(movieId: Int): DocumentReference =
        firestore.collection("movie_ratings")
            .document(movieId.toString())

    override suspend fun addReview(movieId: Int, rating: Float, comment: String): Boolean {
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
                    REVIEW_ID to review.reviewId,
                    MOVIE_ID to review.movieId,
                    USER_ID to review.userId,
                    USER_EMAIL to review.userEmail,
                    RATING to review.rating,
                    COMMENT to review.comment,
                    TIMESTAMP to review.timestamp
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

    override suspend fun updateReview(movieId: Int, rating: Float, comment: String): Boolean {
        return try {
            val currentUser: FirebaseUser? = auth.currentUser
            if (currentUser != null) {
                val reviewId = "${currentUser.uid}_${movieId}"
                val timestamp: Long = System.currentTimeMillis()

                val updateData = hashMapOf(
                    RATING to rating,
                    COMMENT to comment,
                    TIMESTAMP to timestamp
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

    override suspend fun getUserReview(movieId: Int): MovieReview? {
        return try {
            val currentUser: FirebaseUser? = auth.currentUser
            if (currentUser != null) {
                val reviewId = "${currentUser.uid}_${movieId}"
                val document: DocumentSnapshot = getMovieReviewsCollection(movieId)
                    .document(reviewId)
                    .get()
                    .await()

                if (document.exists()) {
                    MovieReview(
                        reviewId = document.getString(REVIEW_ID),
                        movieId = document.getLong(MOVIE_ID)?.toInt(),
                        userId = document.getString(USER_ID),
                        userEmail = document.getString(USER_EMAIL),
                        rating = document.getDouble(RATING)?.toFloat(),
                        comment = document.getString(COMMENT),
                        timestamp = document.getLong(TIMESTAMP)
                    )
                } else
                    null
            } else
                null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMovieReviews(movieId: Int): List<MovieReview> {
        return try {
            val querySnapshot = getMovieReviewsCollection(movieId)
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                try {
                    MovieReview(
                        reviewId = document.getString(REVIEW_ID),
                        movieId = document.getLong(MOVIE_ID)?.toInt(),
                        userId = document.getString(USER_ID),
                        userEmail = document.getString(USER_EMAIL),
                        rating = document.getDouble(RATING)?.toFloat(),
                        comment = document.getString(COMMENT),
                        timestamp = document.getLong(TIMESTAMP)
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieRatingSummary(movieId: Int): MovieRatingSummary? {
        return try {
            val document: DocumentSnapshot = getMovieRatingSummaryDocument(movieId)
                .get()
                .await()

            if (document.exists()) {
                MovieRatingSummary(
                    movieId = document.getLong(MOVIE_ID)?.toInt(),
                    averageRating = document.getDouble(AVERAGE_RATING)?.toFloat(),
                    totalReviews = document.getLong(TOTAL_REVIEWS)?.toInt(),
                    ratingDistribution = document.get(RATING_DISTRIBUTION) as? Map<Int, Int>
                )
            } else
                null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun updateRatingSummary(movieId: Int) {
        try {
            val reviews: List<MovieReview> = getMovieReviews(movieId)

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
                    MOVIE_ID to movieId,
                    AVERAGE_RATING to averageRating,
                    TOTAL_REVIEWS to totalReviews,
                    RATING_DISTRIBUTION to ratingDistribution
                )

                getMovieRatingSummaryDocument(movieId)
                    .set(summaryData)
                    .await()
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun deleteReview(movieId: Int): Boolean {
        return try {
            val currentUser: FirebaseUser? = auth.currentUser
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