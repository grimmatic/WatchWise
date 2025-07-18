package com.vakifbank.WatchWise.data.service

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.utils.Constant
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiTMDB {

    companion object {
        private const val API_KEY = Constant.ApiConstants.TMDB_API_KEY
        private const val API_KEY_PARAM = Constant.ApiConstants.PARAM_API_KEY
        private const val PAGE_PARAM = Constant.ApiConstants.PARAM_PAGE
        private const val LANGUAGE_PARAM = Constant.ApiConstants.PARAM_LANGUAGE
        private const val QUERY_PARAM = Constant.ApiConstants.PARAM_QUERY
        private const val LANGUAGE_TR = Constant.ApiConstants.LANGUAGE_TR
        private const val LANGUAGE_EN = Constant.ApiConstants.LANGUAGE_EN
        private const val DEFAULT_PAGE = Constant.ApiConstants.DEFAULT_PAGE
    }

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query(API_KEY_PARAM) apiKey: String = API_KEY,
        @Query(PAGE_PARAM) page: Int,
        @Query(LANGUAGE_PARAM) language: String = LANGUAGE_TR
    ): GetMoviesResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query(API_KEY_PARAM) apiKey: String = API_KEY,
        @Query(PAGE_PARAM) page: Int,
        @Query(LANGUAGE_PARAM) language: String = LANGUAGE_TR
    ): GetMoviesResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query(API_KEY_PARAM) apiKey: String = API_KEY,
        @Query(PAGE_PARAM) page: Int,
        @Query(LANGUAGE_PARAM) language: String = LANGUAGE_TR
    ): GetMoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int?,
        @Query(API_KEY_PARAM) apiKey: String = API_KEY,
        @Query(LANGUAGE_PARAM) language: String = LANGUAGE_TR
    ): MovieDetail

    @GET("movie/{movie_id}")
    suspend fun getMovieDetailsInEnglish(
        @Path("movie_id") movieId: Int?,
        @Query(API_KEY_PARAM) apiKey: String = API_KEY,
        @Query(LANGUAGE_PARAM) language: String = LANGUAGE_EN
    ): MovieDetail

    @GET("search/movie")
    suspend fun searchMovies(
        @Query(API_KEY_PARAM) apiKey: String = API_KEY,
        @Query(QUERY_PARAM) query: String,
        @Query(LANGUAGE_PARAM) language: String = LANGUAGE_TR,
        @Query(PAGE_PARAM) page: Int = DEFAULT_PAGE
    ): GetMoviesResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query(API_KEY_PARAM) apiKey: String = API_KEY,
        @Query(LANGUAGE_PARAM) language: String = LANGUAGE_EN
    ): MovieVideosResponse
}