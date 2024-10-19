package com.github.diarmaidlindsay.myanimequiz.data.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.nio.charset.StandardCharsets

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        // Log request URL
        Timber.d("Request: ${request.method()} ${request.url()}")

        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e, "HTTP request failed")
            throw e
        }

        // Log response status code and URL
        Timber.d("Response: ${response.code()} ${response.request().url()}")

        // Optionally log part of the response body if needed
        response.body()?.let { body ->
            val source = body.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer.clone()

            val responseBodyString = buffer.readString(StandardCharsets.UTF_8)
            val truncatedBody = responseBodyString.take(200) // Log only the first 200 characters
            Timber.d("Response body (truncated): $truncatedBody...")
        }

        return response
    }
}

class CacheLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val cacheResponse = response.cacheResponse()
        val networkResponse = response.networkResponse()

        if (cacheResponse != null) {
            Timber.d("Response from cache")
        } else if (networkResponse != null) {
            Timber.d("Response from network")
        }

        return response
    }
}