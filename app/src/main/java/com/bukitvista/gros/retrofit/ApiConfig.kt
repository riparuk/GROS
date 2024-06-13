package com.bukitvista.gros.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object{
//        val API_KEY = BuildConfig.API_KEY
        val BASE_URL = "http://34.46.130.76:8000"
        fun getApiService(): ApiService {
            // Buat interceptor untuk logging
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//            val authInterceptor = Interceptor { chain ->
//                val req = chain.request()
//                val requestHeaders = req.newBuilder()
//                    .addHeader("Authorization", API_KEY)
//                    .build()
//                chain.proceed(requestHeaders)
//            }
            val client = OkHttpClient.Builder()
//                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}