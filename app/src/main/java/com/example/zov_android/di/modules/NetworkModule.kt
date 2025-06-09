package com.example.zov_android.di.modules

import com.example.zov_android.data.api.ApiService
import com.example.zov_android.data.api.RetrofitClient
import com.example.zov_android.di.qualifiers.Token
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton


// при попытке получить экземпляр ApiService Dagger будет смотреть на этот модуль

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Token token: String
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }


}