package com.example.cityreporter.di

import com.example.cityreporter.BuildConfig
import com.example.cityreporter.data.api.CityReporterApi
import com.example.cityreporter.data.api.AuthInterceptor
import com.example.cityreporter.data.preferences.UserPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // For development: Use 10.0.2.2 for Android emulator or your computer's IP
    private const val BASE_URL = "http://10.0.2.2:8080/"
    // For production: Replace with your actual server URL
    // private const val BASE_URL = "https://your-server.com/"
    
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()
    
    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferences: UserPreferences): AuthInterceptor {
        return AuthInterceptor(userPreferences)
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideCityReporterApi(retrofit: Retrofit): CityReporterApi {
        return retrofit.create(CityReporterApi::class.java)
    }
}