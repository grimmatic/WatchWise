package com.vakifbank.WatchWise.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieDetail(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("poster_path") val poster: String?,
    @SerializedName("backdrop_path") val backdrop: String?,
    @SerializedName("overview") val description: String?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val rating: Float?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("original_language") val language: String?,
    @SerializedName("budget") val budget: Long?,
    @SerializedName("revenue") val revenue: Long?,
    @SerializedName("genres") val genres: List<Genre>?,
) : Parcelable


@Parcelize
data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) : Parcelable

