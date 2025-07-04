package com.vakifbank.WatchWise.data.api

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.ui.activity.Constanst
import retrofit2.http.GET
import retrofit2.http.Query

interface Api_TMDB {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = Constanst.apiConstansts.TMDB_API_KEY
        ,@Query("page") page: Int
    ): retrofit2.Call<GetMoviesResponse>

}