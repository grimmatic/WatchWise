package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(movieId: Int?) = movieRepository.getMovieDetails(movieId)
}