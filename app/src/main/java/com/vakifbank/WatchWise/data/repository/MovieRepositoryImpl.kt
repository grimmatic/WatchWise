package com.vakifbank.WatchWise.data.repository

import com.vakifbank.WatchWise.data.service.ApiTMDB
import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: ApiTMDB
) : MovieRepository {

    override fun getPopularMovies(page: Int): Call<GetMoviesResponse> {
        return api.getPopularMovies(page = page)
    }

    override fun getTopRatedMovies(page: Int): Call<GetMoviesResponse> {
        return api.getTopRatedMovies(page = page)
    }

    override fun getUpcomingMovies(page: Int): Call<GetMoviesResponse> {
        return api.getUpcomingMovies(page = page)
    }

    override fun getMovieDetails(movieId: Int?): Call<MovieDetail> {
        return api.getMovieDetails(movieId = movieId)
    }

    override fun getMovieDetailsInEnglish(movieId: Int?): Call<MovieDetail> {
        return api.getMovieDetailsInEnglish(movieId = movieId)
    }

    override fun searchMovies(query: String, page: Int): Call<GetMoviesResponse> {
        return api.searchMovies(query = query, page = page)
    }

    override fun getMovieVideos(movieId: Int): Call<MovieVideosResponse> {
        return api.getMovieVideos(movieId = movieId)
    }
}