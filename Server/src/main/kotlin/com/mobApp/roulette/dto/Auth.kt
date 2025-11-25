package com.mobApp.roulette.dto

data class SignUpRequest(
        val userId: String,
        val password: String,
        val nickname: String
)
data class SignUpResponse(
        val id: Long,
        val userId: String,
        val nickname: String
)
data class LoginRequest(
        val userId: String,
        val password: String
)
data class LoginResponse(
        val accessToken: String,
        val refreshToken: String,
        val id: Long,
        val nickname: String
)
data class TokenRefreshRequest(
        val refreshToken: String
)
data class TokenRefreshResponse(
        val accessToken: String
)