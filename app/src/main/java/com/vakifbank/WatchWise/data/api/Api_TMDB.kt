package com.vakifbank.WatchWise.data.api

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.ui.activity.Constant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiTMDB {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstansts.TMDB_API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "tr-TR"
    ): Call<GetMoviesResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstansts.TMDB_API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "tr-TR"
    ): Call<GetMoviesResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstansts.TMDB_API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "tr-TR"
    ): Call<GetMoviesResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String = Constant.ApiConstansts.TMDB_API_KEY,
        @Query("language") language: String = "tr-TR"
    ): Call<MovieDetail>

    @GET("movie/{movie_id}")
    fun getMovieDetailsInEnglish(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String = Constant.ApiConstansts.TMDB_API_KEY,
        @Query("language") language: String = "en-US"
    ): Call<MovieDetail>


    @GET("search/movie")
    fun searchMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstansts.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("language") language: String = "tr-TR",
        @Query("page") page: Int = 1
    ): Call<GetMoviesResponse>

    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constant.ApiConstansts.TMDB_API_KEY,
        @Query("language") language: String = "en-US"
    ): Call<MovieVideosResponse>

}