package com.example.todoapp.di.modules

import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.data.network.ToDoAPI
import com.example.todoapp.data.util.SharedPreferenceHelper
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideApiUrl(): String = "https://beta.mrdekk.ru/todobackend/"

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideNetworkClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer leuchemia")
            .build()
        chain.proceed(newRequest)
    }.addInterceptor(HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }).build()

    @Provides
    @Singleton
    fun provideRetrofit(
        url: String,
        factory: GsonConverterFactory,
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(factory)
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ToDoAPI = retrofit.create(ToDoAPI::class.java)

    @Provides
    @Singleton
    fun provideNetworkSource(committer: SharedPreferenceHelper, api: ToDoAPI) = NetworkRepository(committer, api)
}