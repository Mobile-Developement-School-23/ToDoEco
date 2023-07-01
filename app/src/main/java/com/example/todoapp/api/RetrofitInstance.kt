package com.example.todoapp.api

import com.example.todoapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    private val token = "leuchemia"

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)

    }

}

class RetrofitInstance {

    companion object {

        private val retrofit by lazy {

            val tokenInterceptor = AuthorizationInterceptor()

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        val api by lazy {

            retrofit.create(TaskAPI::class.java)

        }

    }

}