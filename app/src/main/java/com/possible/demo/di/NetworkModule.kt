package com.possible.demo.di

import com.possible.demo.data.shared.Clock
import com.possible.demo.data.shared.ClockImpl
import com.possible.demo.data.shared.GitHubApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Module defining the setup of network related classes.
 * @param baseUrl the baseUrl of the [Retrofit].
 */
@Module
class NetworkModule(private val baseUrl: String) {

    @Provides
    fun provideOkHttp(): OkHttpClient =
            OkHttpClient.Builder().build()

    @Provides
    fun provideConverterFactory(): Converter.Factory =
            GsonConverterFactory.create()

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit =
            Retrofit.Builder()
                    .addConverterFactory(converterFactory)
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .build()

    @Provides
    @Singleton
    fun provideGitHubService(retrofit: Retrofit): GitHubApi = retrofit.create()

    @Provides
    fun provideClock(clockImpl: ClockImpl): Clock = clockImpl

    companion object {
        private inline fun <reified T> Retrofit.create(): T = create(T::class.java)
    }
}