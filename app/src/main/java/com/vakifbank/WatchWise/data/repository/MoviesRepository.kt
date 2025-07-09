package com.vakifbank.WatchWise.data.repository

import com.vakifbank.WatchWise.data.api.Api_TMDB
import com.vakifbank.WatchWise.ui.activity.Constanst
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoviesRepository {
    private val api: Api_TMDB

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constanst.apiConstansts.TMBD_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api_TMDB::class.java)
    }

    fun getPopularMovies(page: Int) = api.getPopularMovies(page = page)
    fun getTopRatedMovies(page: Int) = api.getTopRatedMovies(page = page)
    fun getUpcomingMovies(page: Int) = api.getUpcomingMovies(page = page)
    fun getMovieDetails(movieId: Int?) = api.getMovieDetails(movieId = movieId)
    fun searchMovies(query: String, page: Int = 1) = api.searchMovies(query = query, page = page)
    fun getMovieVideos(movieId: Int) = api.getMovieVideos(movieId = movieId)

}