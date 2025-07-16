package com.vakifbank.WatchWise.data.repository

import com.vakifbank.WatchWise.data.api.ApiTMDB
import com.vakifbank.WatchWise.utils.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoviesRepository {
    private val api: ApiTMDB

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.ApiConstants.TMBD_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiTMDB::class.java)
    }

    fun getPopularMovies(page: Int) = api.getPopularMovies(page = page)
    fun getTopRatedMovies(page: Int) = api.getTopRatedMovies(page = page)
    fun getUpcomingMovies(page: Int) = api.getUpcomingMovies(page = page)
    fun getMovieDetails(movieId: Int?) = api.getMovieDetails(movieId = movieId)
    fun getMovieDetailsInEnglish(movieId: Int?) = api.getMovieDetailsInEnglish(movieId = movieId)
    fun searchMovies(query: String, page: Int = 1) = api.searchMovies(query = query, page = page)
    fun getMovieVideos(movieId: Int) = api.getMovieVideos(movieId = movieId)

}