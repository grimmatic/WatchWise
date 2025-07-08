package com.vakifbank.WatchWise.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieVideosResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("results") val results: List<MovieVideo>?
) : Parcelable

@Parcelize
data class MovieVideo(
    @SerializedName("id") val id: String?,
    @SerializedName("key") val key: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("site") val site: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("official") val official: Boolean?,
    @SerializedName("published_at") val publishedAt: String?
) : Parcelable