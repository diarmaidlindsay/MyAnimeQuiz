package com.github.diarmaidlindsay.myanimequiz.di

import com.github.diarmaidlindsay.myanimequiz.BuildConfig
import com.github.diarmaidlindsay.myanimequiz.data.network.LoggingInterceptor
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.data.service.ApiService
import com.github.diarmaidlindsay.myanimequiz.network.AuthInterceptor
import com.github.diarmaidlindsay.myanimequiz.utils.Constants.MAL_API_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferencesRepository: UserPreferencesRepository): AuthInterceptor {
        return AuthInterceptor(userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: LoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MAL_API_URL)
            .client(okHttpClient)
            .addConverterFactory(
                Json.asConverterFactory(
                    MediaType.get("application/json; charset=UTF8")
                )
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}