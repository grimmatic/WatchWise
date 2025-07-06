package com.vakifbank.WatchWise.domain.model

import com.google.gson.annotations.SerializedName

data class Movie(@SerializedName("title") val title: String?,
                 @SerializedName("poster_path") val poster: String?,
                 @SerializedName("id") val id: Int?,
                 @SerializedName("genre_ids") val genres: List<Int>?,
                 @SerializedName("release_date") val year: String?,
                 @SerializedName("overview") val description: String?,
                 @SerializedName("vote_average") val rating: Float?,
                 @SerializedName("original_language") val language: String?)