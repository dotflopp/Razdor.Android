package com.example.zov_android.di.modules

import android.content.Context
import com.example.zov_android.data.signalr.SignalR
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SignalRModule {

    @Provides
    @Singleton
    fun provideSignalR(
        @ApplicationContext context: Context,
    ): SignalR {
        val url = "http://26.101.132.34:5154/signaling"
        return SignalR(context, url)
    }
}