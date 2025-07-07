package com.vakifbank.WatchWise.domain.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
                 @SerializedName("title") val title: String?,
                 @SerializedName("poster_path") val poster: String?,
                 @SerializedName("id") val id: Int?,
                 @SerializedName("genre_ids") val genres: List<Int>?,
                 @SerializedName("release_date") val year: String?,
                 @SerializedName("overview") val description: String? = null,
                 @SerializedName("vote_average") val rating: Float?,
                 @SerializedName("original_language") val language: String?
) : Parcelable