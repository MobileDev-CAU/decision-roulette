package com.example.decisionroulette.ui.auth

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "AuthPrefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_USER_ID = "user_id"

    private lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ⭐ 추가: 사용자 ID를 저장하는 함수
    fun setUserId(userId: Int) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            apply()
        }
    }

    // 1. 토큰 및 사용자 정보 저장 (로그인 성공 시)
    fun saveTokensAndUser(accessToken: String, refreshToken: String, nickname: String, userId: Int) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_NICKNAME, nickname)
            putInt(KEY_USER_ID, userId)
            apply()
        }
        // ⭐ 참고: 여기에도 setUserId를 추가할 수 있지만, AuthRepository에서 처리하는 것이 일반적입니다.
    }

    // 2. 토큰 없이 사용자 정보만 저장 (회원가입 성공 시)
    fun saveUser(email: String, nickname: String) {
        prefs.edit().apply {
            putString(KEY_USER_EMAIL, email)
            putString(KEY_NICKNAME, nickname)
            apply()
        }
    }

    // 3. 사용자 정보 가져오기 (ViewModel 초기화 시 호출)
    fun getUserInfo(): Pair<String?, String?> {
        val email = prefs.getString(KEY_USER_EMAIL, null)
        val nickname = prefs.getString(KEY_NICKNAME, null)
        return Pair(email, nickname)
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    // 4. 토큰 삭제 (로그아웃 시)
    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getUserNickname(): String? {
        return prefs.getString(KEY_NICKNAME, null)
    }

}