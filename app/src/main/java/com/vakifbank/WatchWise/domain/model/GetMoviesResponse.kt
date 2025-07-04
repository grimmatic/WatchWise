package com.vakifbank.WatchWise.domain.model

import com.google.gson.annotations.SerializedName

data class GetMoviesResponse(
    @SerializedName("results") val movies: List<Movie>? = null,
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int
)