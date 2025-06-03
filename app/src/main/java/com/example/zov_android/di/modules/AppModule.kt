package com.example.zov_android.di.modules

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module //класс одноэлементных компонентов // Аннотация, указывающая, что класс предоставляет зависимости
@InstallIn(SingletonComponent::class)
class AppModule {
    //возращает контекст приложения
    @Provides //Каждый метод, помеченный этой аннотацией, должен возвращать объект, который будет предоставлен.
    fun provideContext(@ApplicationContext context: Context):Context = context.applicationContext

    @Provides
    fun provideGson(): Gson = Gson();


}