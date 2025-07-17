package com.vakifbank.WatchWise.domain.repository

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): GetMoviesResponse
    suspend fun getTopRatedMovies(page: Int): GetMoviesResponse
    suspend fun getUpcomingMovies(page: Int): GetMoviesResponse
    suspend fun getMovieDetails(movieId: Int?): MovieDetail
    suspend fun getMovieDetailsInEnglish(movieId: Int?): MovieDetail
    suspend fun searchMovies(query: String, page: Int = 1): GetMoviesResponse
    suspend fun getMovieVideos(movieId: Int): MovieVideosResponse
}