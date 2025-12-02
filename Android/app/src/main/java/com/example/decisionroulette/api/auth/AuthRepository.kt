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
            Log.e("AUTH_FAIL", "네트워크 오류 발생: ${e.message}")
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
                response.body()?.let { loginResponse -> // ⭐ 응답 DTO를 loginResponse로 받음
                    // ⭐ 수정: TokenManager의 새로운 시그니처에 맞춰 userId(id)를 전달
                    TokenManager.saveTokensAndUser(
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken,
                        nickname = loginResponse.nickname,
                        userId = loginResponse.id // ⭐ LoginResponse의 'id'를 userId로 전달
                    )

                    // 🚨 제거: saveTokensAndUser에서 이미 ID를 저장하므로 중복 호출 제거
                    // TokenManager.setUserId(loginResponse.id)

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

    // ⭐ 추가: VoteViewModel에서 사용하는 userId 가져오는 함수
    /**
     * 로컬 저장소(TokenManager)에서 현재 로그인된 사용자의 ID를 가져옵니다.
     * @return 유효한 userId(Int) 또는 인증되지 않았을 경우 null
     */
    fun getCurrentUserId(): Int? {
        val userId = TokenManager.getUserId()
        // TokenManager에서 -1을 반환하는 경우 유효하지 않은 것으로 간주
        return if (userId > 0) userId else null
    }
}