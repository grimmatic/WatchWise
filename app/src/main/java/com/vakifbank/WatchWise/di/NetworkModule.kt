package com.vakifbank.WatchWise.di

import com.vakifbank.WatchWise.data.service.ApiTMDB
import com.vakifbank.WatchWise.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.ApiConstants.TMBD_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiTMDB(retrofit: Retrofit): ApiTMDB {
        return retrofit.create(ApiTMDB::class.java)
    }

    @Singleton
    @Provides
    fun provideRestOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            readTimeout(Constant.TimeOutConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(Constant.TimeOutConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
        }.build()
    }
}