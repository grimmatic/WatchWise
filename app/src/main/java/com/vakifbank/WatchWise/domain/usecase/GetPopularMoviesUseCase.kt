package com.vakifbank.WatchWise.domain.usecase

import com.vakifbank.WatchWise.domain.model.GetMoviesResponse
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import retrofit2.Call
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(page: Int): Call<GetMoviesResponse> {
        return movieRepository.getPopularMovies(page)
    }
}