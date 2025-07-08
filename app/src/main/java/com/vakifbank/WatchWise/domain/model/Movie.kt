package com.vakifbank.WatchWise.domain.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
                 @SerializedName("title") val title: String?,
                 @SerializedName("poster_path") val poster: String?,
                 @SerializedName("overview") val description: String?,
                 @SerializedName("id") val id: Int?,
                 @SerializedName("vote_average") val rating: Float?,


) : Parcelable