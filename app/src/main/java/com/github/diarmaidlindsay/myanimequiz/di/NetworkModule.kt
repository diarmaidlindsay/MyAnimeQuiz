package com.github.diarmaidlindsay.myanimequiz.di

import android.content.Context
import com.github.diarmaidlindsay.myanimequiz.BuildConfig
import com.github.diarmaidlindsay.myanimequiz.data.network.AuthInterceptor
import com.github.diarmaidlindsay.myanimequiz.data.network.CacheControlInterceptor
import com.github.diarmaidlindsay.myanimequiz.data.network.CacheLoggingInterceptor
import com.github.diarmaidlindsay.myanimequiz.data.network.LoggingInterceptor
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import com.github.diarmaidlindsay.myanimequiz.data.service.ApiService
import com.github.diarmaidlindsay.myanimequiz.utils.Constants.MAL_API_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
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
    fun provideCacheLoggingInterceptor(): CacheLoggingInterceptor {
        return CacheLoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferencesRepository: UserPreferencesRepository): AuthInterceptor {
        return AuthInterceptor(userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideCacheControlInterceptor(): CacheControlInterceptor {
        return CacheControlInterceptor()
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cacheDir = File(context.cacheDir, "http_cache")
        return Cache(cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: LoggingInterceptor,
        cacheLoggingInterceptor: CacheLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        cacheControlInterceptor: CacheControlInterceptor,
        cache: Cache
    ): OkHttpClient {

        val builder = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            .addInterceptor(cacheControlInterceptor)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor)
            builder.addInterceptor(cacheLoggingInterceptor)
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
