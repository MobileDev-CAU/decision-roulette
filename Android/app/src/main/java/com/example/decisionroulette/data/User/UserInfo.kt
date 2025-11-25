package com.example.decisionroulette.data.User

// /auth/signup

data class SignUpRequest(

    val userId: String,
    val password:String,
    val nickname:String
)

data class SignUpResponse(

    val id: Int,
    val userId: String,
    val nickname:String

)

// /auth/login

data class LoginRequest(

    val userId: String,
    val password: String
)

data class LoginResponse(

    val accessToken:String,
    val refreshToken: String,
    val id:Int,
    val nickname:String
)

// /auth/refresh

data class RefreshRequest(

    val refreshToken:String
)

data class RefreshResponse(

    val accessToken:String
)


