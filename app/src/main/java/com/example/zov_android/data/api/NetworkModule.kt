package com.example.zov_android.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


// при попытке получить экземпляр ApiService Dagger будет смотреть на этот модуль

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }
}