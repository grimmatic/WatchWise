package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.model.MovieDetail
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import retrofit2.Call
import javax.inject.Inject

class GetMovieDetailsInEnglishUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(movieId: Int?): Call<MovieDetail> {
        return movieRepository.getMovieDetailsInEnglish(movieId)
    }
}