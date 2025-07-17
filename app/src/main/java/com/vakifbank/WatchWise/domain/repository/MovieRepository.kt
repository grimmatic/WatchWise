package com.vakifbank.WatchWise.domain.repository

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import retrofit2.Call

interface MovieRepository {
    fun getPopularMovies(page: Int): Call<GetMoviesResponse>
    fun getTopRatedMovies(page: Int): Call<GetMoviesResponse>
    fun getUpcomingMovies(page: Int): Call<GetMoviesResponse>
    fun getMovieDetails(movieId: Int?): Call<MovieDetail>
    fun getMovieDetailsInEnglish(movieId: Int?): Call<MovieDetail>
    fun searchMovies(query: String, page: Int = 1): Call<GetMoviesResponse>
    fun getMovieVideos(movieId: Int): Call<MovieVideosResponse>
}