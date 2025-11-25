package com.example.decisionroulette.ui.auth

data class AuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val passwordConfirmInput: String = "", // ⬅️ 비밀번호 확인 필드
    val isLoginLoading: Boolean = false,
    val loginError: String? = null
)
