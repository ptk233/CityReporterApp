package com.example.cityreporter.di

import android.content.Context
import com.example.cityreporter.utils.ImageHelper
import com.example.cityreporter.utils.LocationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideLocationHelper(
        @ApplicationContext context: Context
    ): LocationHelper {
        return LocationHelper(context)
    }
    
    @Provides
    fun provideImageHelper(
        @ApplicationContext context: Context
    ): ImageHelper {
        return ImageHelper(context)
    }
}
