package com.example.zov_android.utils

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    @Provides
    fun provideDateBaseInstance():FirebaseDatabase = FirebaseDatabase.getInstance()

    //получаем объект базы данных и возращаем на него ссылку
    @Provides
    fun provideDatabaseReference(db:FirebaseDatabase): DatabaseReference = db.reference
}