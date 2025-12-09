package com.example.decisionroulette.api.auth

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response


interface AuthApiService {


    @POST("/auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>


    @POST("/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    @POST("/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshRequest
    ): Response<RefreshResponse>
}