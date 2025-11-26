package com.example.cityreporter.data.api

import com.example.cityreporter.data.preferences.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip adding token for auth endpoints
        if (originalRequest.url.encodedPath.contains("/auth/login") ||
            originalRequest.url.encodedPath.contains("/auth/register")) {
            return chain.proceed(originalRequest)
        }
        
        // Get token from DataStore
        val token = runBlocking {
            userPreferences.authToken.firstOrNull()
        }
        
        return if (token != null) {
            val modifiedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(modifiedRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}