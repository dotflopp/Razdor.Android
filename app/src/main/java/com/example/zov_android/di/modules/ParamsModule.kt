package com.example.zov_android.di.modules

import android.content.Context
import com.example.zov_android.di.qualifiers.SessionId
import com.example.zov_android.di.qualifiers.Token
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParamsModule {
    @Provides
    @SessionId
    fun provideSessionId(@ApplicationContext context: Context): String {
        // получение sessionId из SharedPreferences
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("session_id", "default_session_id") ?: "default_session_id"
    }

    @Provides
    @Token
    @Singleton
    fun provideToken(@ApplicationContext context: Context): String{
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("token", null) ?: "null"
    }
}