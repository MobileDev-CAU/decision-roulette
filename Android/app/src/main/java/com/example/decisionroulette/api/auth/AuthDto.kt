package com.example.decisionroulette.api.auth


// /auth/signup
// [요청] 회원 가입할 때 보내는 데이터

data class SignUpRequest(

    val userId: String,
    val password:String,
    val nickname:String
)

// [응답] 회원 가입 성공 시 오는 데이터

data class SignUpResponse(

    val id: Int,
    val userId: String,
    val nickname:String

)

// /auth/login
// [요청] 로그인할 때 보내는 데이터

data class LoginRequest(

    val userId: String,
    val password: String
)

// [응답] 로그인 성공 시 오는 데이터

data class LoginResponse(

    val accessToken:String,
    val refreshToken: String,
    val id:Int,
    val nickname:String
)

// /auth/refresh
// [요청] 접근 토큰(AccessToken) 만료 시 재발급을 위해 보내는 데이터

data class RefreshRequest(

    val refreshToken:String
)

// [응답] 새로운 접근 토큰 재발급 성공 시 오는 데이터

data class RefreshResponse(

    val accessToken:String
)


