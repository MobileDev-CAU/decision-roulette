package com.example.decisionroulette.api

import com.example.decisionroulette.api.roulette.RouletteApiService
import com.example.decisionroulette.api.auth.AuthApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.decisionroulette.ui.auth.TokenManager

object RetrofitClient {
    private const val BASE_URL = "http://15.165.9.218:8081/"

    // 🚨🚨🚨 Access Token 첨부 로직 추가된 Interceptor 🚨🚨🚨
    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // 1. TokenManager에서 Access Token을 가져옵니다.
        val accessToken = TokenManager.getAccessToken()

        // 2. 새로운 요청 빌더를 시작합니다.
        val requestBuilder = originalRequest.newBuilder()
            .header("accept", "*/*")

        // 3. Access Token이 존재하면 Authorization 헤더를 추가합니다.
        if (accessToken != null) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        val newRequest = requestBuilder.build()
        // ----------------------------------------------------

        chain.proceed(newRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 통신 내용 다 보여줌
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor) // Access Token Interceptor 적용
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

    val authInstance: AuthApiService by lazy {
        // 인증 관련 API (로그인, 회원가입)에도 토큰이 필요 없으면(일반적) headerInterceptor를 그대로 사용합니다.
        // 하지만 로그인이 필요한 경우 토큰이 있어도 괜찮습니다.
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}