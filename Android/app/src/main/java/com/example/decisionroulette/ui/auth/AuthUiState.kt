package com.example.decisionroulette.ui.auth

data class AuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val nicknameInput: String = "",
    val isLoginLoading: Boolean = false,
    val loginError: String? = null,
    val isLoggedIn: Boolean = false
)
