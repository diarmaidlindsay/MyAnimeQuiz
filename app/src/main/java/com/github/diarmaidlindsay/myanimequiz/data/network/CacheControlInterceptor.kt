package com.github.diarmaidlindsay.myanimequiz.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add cache control header to responses
 * MAL API does not cache responses so we add a cache control header to cache responses for 24 hours.
 * User will be given the option to disable this if they wish.
 */
class CacheControlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        return response.newBuilder()
            .header("Cache-Control", "public, max-age=${60 * 60 * 24}") // 24 hours
            .build()
    }
}