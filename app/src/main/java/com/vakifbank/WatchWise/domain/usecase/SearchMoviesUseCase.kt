package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): GetMoviesResponse {
        return movieRepository.searchMovies(query, page)
    }
}