package com.vakifbank.WatchWise.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vakifbank.WatchWise.domain.model.Movie
import kotlinx.coroutines.tasks.await

object FavoriteRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun getUserFavoritesCollection() =
        firestore.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("favorites")

    suspend fun addToFavorites(movie: Movie): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null && movie.id != null) {
                val favoriteMovie = hashMapOf(
                    "id" to movie.id,
                    "title" to movie.title,
                    "poster" to movie.poster,
                    "description" to movie.description,
                    "rating" to movie.rating,
                    "tagline" to movie.tagline,
                    "addedAt" to System.currentTimeMillis()
                )

                getUserFavoritesCollection()
                    .document(movie.id.toString())
                    .set(favoriteMovie)
                    .await()
                true
            } else
                false

        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromFavorites(movieId: Int): Boolean {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                getUserFavoritesCollection()
                    .document(movieId.toString())
                    .delete()
                    .await()
                true
            } else
                false

        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFavoriteMovies(): List<Movie> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val querySnapshot = getUserFavoritesCollection()
                    .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                querySnapshot.documents.mapNotNull { document ->
                    try {
                        Movie(
                            id = document.getLong("id")?.toInt(),
                            title = document.getString("title"),
                            poster = document.getString("poster"),
                            description = document.getString("description"),
                            rating = document.getDouble("rating")?.toFloat(),
                            tagline = document.getString("tagline")
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            } else
                emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun isMovieFavorite(movieId: Int): Boolean {
        return try {
            val currentUser = auth.currentUser

            currentUser?.let {
                val document = getUserFavoritesCollection()
                    .document(movieId.toString())
                    .get()
                    .await()
                document.exists()
            }?:false

            /*if (currentUser != null) {
                // != null ?.let
                val document = getUserFavoritesCollection()
                    .document(movieId.toString())
                    .get()
                    .await()
                document.exists()
            } else
                false*/

        } catch (e: Exception) {
            false
        }
    }
}