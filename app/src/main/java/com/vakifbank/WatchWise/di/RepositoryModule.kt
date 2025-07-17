package com.vakifbank.WatchWise.di

import com.vakifbank.WatchWise.data.repository.FavoriteRepositoryImpl
import com.vakifbank.WatchWise.data.repository.MovieRepositoryImpl
import com.vakifbank.WatchWise.data.repository.ReviewRepositoryImpl
import com.vakifbank.WatchWise.domain.repository.FavoriteRepository
import com.vakifbank.WatchWise.domain.repository.MovieRepository
import com.vakifbank.WatchWise.domain.repository.ReviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMovieRepository(
        movieRepositoryImpl: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository
}