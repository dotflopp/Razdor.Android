package com.example.zov_android.di.modules

import com.example.zov_android.data.api.ApiService
import com.example.zov_android.data.api.RetrofitClient
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