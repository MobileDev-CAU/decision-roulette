package com.example.decisionroulette.api

import com.example.decisionroulette.api.roulette.RouletteApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://15.165.9.218:8081/"

    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // 기존 요청에 헤더를 추가한 새로운 요청 생성
        val newRequest = originalRequest.newBuilder()
            .header("accept", "*/*")
            // .header("Authorization", "Bearer $token") // 나중에 토큰 필요하면 여기에 추가
            .build()

        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 통신 내용 다 보여줌
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: RouletteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RouletteApiService::class.java)
    }
}