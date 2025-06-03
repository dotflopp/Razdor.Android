package com.example.zov_android.di.modules

import android.content.Context
import com.example.zov_android.data.models.response.UserResponse
import com.example.zov_android.di.qualifiers.User
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @User
    @Singleton
    fun provideUsername(@ApplicationContext context: Context): UserResponse{
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("user",null)
        return Gson().fromJson(json,UserResponse::class.java)
    }
}