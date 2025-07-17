package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import javax.inject.Inject

class GetUpcomingMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(page: Int): GetMoviesResponse {
        return movieRepository.getUpcomingMovies(page)
    }
}