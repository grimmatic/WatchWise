package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.model.MovieVideosResponse
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): MovieVideosResponse {
        return movieRepository.getMovieVideos(movieId)
    }
}