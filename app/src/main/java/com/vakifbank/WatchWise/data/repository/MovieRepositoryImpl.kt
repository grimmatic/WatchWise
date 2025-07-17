package com.vakifbank.WatchWise.data.repository

import com.vakifbank.WatchWise.data.service.ApiTMDB
import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: ApiTMDB
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): GetMoviesResponse {
        return api.getPopularMovies(page = page)
    }

    override suspend fun getTopRatedMovies(page: Int): GetMoviesResponse {
        return api.getTopRatedMovies(page = page)
    }

    override suspend fun getUpcomingMovies(page: Int): GetMoviesResponse {
        return api.getUpcomingMovies(page = page)
    }

    override suspend fun getMovieDetails(movieId: Int?): MovieDetail {
        return api.getMovieDetails(movieId = movieId)
    }

    override suspend fun getMovieDetailsInEnglish(movieId: Int?): MovieDetail {
        return api.getMovieDetailsInEnglish(movieId = movieId)
    }

    override suspend fun searchMovies(query: String, page: Int): GetMoviesResponse {
        return api.searchMovies(query = query, page = page)
    }

    override suspend fun getMovieVideos(movieId: Int): MovieVideosResponse {
        return api.getMovieVideos(movieId = movieId)
    }
}