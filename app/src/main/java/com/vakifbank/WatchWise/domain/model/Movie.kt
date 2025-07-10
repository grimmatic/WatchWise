package com.vakifbank.WatchWise.domain.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
                 @SerializedName("title") val title: String?,
                 @SerializedName("poster_path") val poster: String?,
                 @SerializedName("overview") val description: String?,
                 @SerializedName("tagline") val tagline: String?,
                 @SerializedName("id") val id: Int?,
                 @SerializedName("vote_average") val rating: Float?,
    ) : Parcelable