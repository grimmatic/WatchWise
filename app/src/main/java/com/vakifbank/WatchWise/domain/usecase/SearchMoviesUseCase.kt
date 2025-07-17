package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(query: String, page: Int = 1) = movieRepository.searchMovies(query, page)
}