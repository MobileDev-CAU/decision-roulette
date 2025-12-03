package com.example.decisionroulette.api.auth

import android.util.Log
import com.example.decisionroulette.api.RetrofitClient
import com.example.decisionroulette.ui.auth.TokenManager
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class AuthRepository(private val api: AuthApiService = RetrofitClient.authInstance) {

    // 회원가입
    suspend fun signUp(request: SignUpRequest): Result<SignUpResponse> {
        return try {
            val response = api.signUp(request)

            if (response.isSuccessful) {

                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Server returned empty body on successful sign up."))
            } else {
                // 클라이언트/서버 오류 (4xx, 5xx): HTTP 예외 처리
                Result.failure(HttpException(response)) // HTTP 에러 발생
            }
        } catch (e: IOException) {
            // 네트워크 오류 (연결 끊김, Timeout 등)
            Log.e("AUTH_FAIL", "Network Error Occurred: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            // 그 외 예외
            Result.failure(e)
        }
    }

    // 로그인
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            // authApiService 대신 api 사용으로 수정됨
            val response = api.login(request)

            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    TokenManager.saveTokensAndUser(
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken,
                        nickname = loginResponse.nickname,
                        userId = loginResponse.id
                    )


                    Result.success(loginResponse)
                } ?: Result.failure(IOException("Server returned empty body on successful login."))
            } else {
                // 클라이언트/서버 오류
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            // 네트워크 오류
            Result.failure(e)
        } catch (e: Exception) {
            // 그 외 예외
            Result.failure(e)
        }
    }

    // 토큰 리프레쉬
    suspend fun refreshToken(request: RefreshRequest): Result<RefreshResponse> {
        return try {
            // authApiService 대신 api 사용으로 수정됨
            val response = api.refreshToken(request)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Server returned empty body on successful token refresh."))
            } else {
                // Refresh Token 만료
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            // 네트워크 오류
            Result.failure(e)
        } catch (e: Exception) {
            // 그 외 예외
            Result.failure(e)
        }
    }

    fun getCurrentUserNickname(): String? {
        return TokenManager.getUserNickname()
    }

    fun getCurrentUserId(): Int? {
        val userId = TokenManager.getUserId()
        // TokenManager에서 -1을 반환하는 경우 유효하지 않은 것으로 간주
        return if (userId > 0) userId else null
    }
}