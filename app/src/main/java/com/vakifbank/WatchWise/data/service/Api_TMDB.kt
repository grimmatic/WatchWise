package com.vakifbank.WatchWise.data.service

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.utils.Constant
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiTMDB {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstants.TMDB_API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "tr-TR"
    ): GetMoviesResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstants.TMDB_API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "tr-TR"
    ): GetMoviesResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstants.TMDB_API_KEY,
        @Query("page") page: Int,
        @Query("language") language: String = "tr-TR"
    ): GetMoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String = Constant.ApiConstants.TMDB_API_KEY,
        @Query("language") language: String = "tr-TR"
    ): MovieDetail

    @GET("movie/{movie_id}")
    suspend fun getMovieDetailsInEnglish(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String = Constant.ApiConstants.TMDB_API_KEY,
        @Query("language") language: String = "en-US"
    ): MovieDetail

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = Constant.ApiConstants.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("language") language: String = "tr-TR",
        @Query("page") page: Int = 1
    ): GetMoviesResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constant.ApiConstants.TMDB_API_KEY,
        @Query("language") language: String = "en-US"
    ): MovieVideosResponse
}