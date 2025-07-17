package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailsInEnglishUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int?): MovieDetail {
        return movieRepository.getMovieDetailsInEnglish(movieId)
    }
}