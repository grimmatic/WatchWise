package com.vakifbank.WatchWise.domain.usecase


import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import retrofit2.Call
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(movieId: Int): Call<MovieVideosResponse> {
        return movieRepository.getMovieVideos(movieId)
    }
}