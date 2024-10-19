package com.github.diarmaidlindsay.myanimequiz.data.network

import com.github.diarmaidlindsay.myanimequiz.BuildConfig
import com.github.diarmaidlindsay.myanimequiz.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferencesRepository: UserPreferencesRepository) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("X-MAL-CLIENT-ID", BuildConfig.CLIENT_ID)

        val accessToken = runBlocking { userPreferencesRepository.accessToken.firstOrNull() }
        accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(requestBuilder.build())
    }
}