package com.example.decisionroulette.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.api.auth.AuthRepository
import com.example.decisionroulette.api.auth.LoginRequest
import com.example.decisionroulette.api.auth.SignUpRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// AuthUiEvent 정의
sealed interface AuthUiEvent {
    object NavigateToLoginSuccess : AuthUiEvent
    object NavigateToSignUp : AuthUiEvent
    object NavigateToLogin : AuthUiEvent
    data class ShowError(val message: String) : AuthUiEvent
}

class AuthViewModel() : ViewModel() {

    private val authRepository = AuthRepository()

    var uiState by mutableStateOf(AuthUiState())
        private set

    private val _events = Channel<AuthUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // 1. 초기화 블록: 앱 시작 시 로컬 저장소에서 토큰을 읽어와 로그인 상태를 복원
    init {
        // Access Token과 사용자 정보가 로컬 저장소에 남아있는지 확인
        val accessToken = TokenManager.getAccessToken()
        val (email, nickname) = TokenManager.getUserInfo()

        // Access Token이 있고 사용자 정보도 있다면 로그인 상태로 간주
        val isUserLoggedIn = !accessToken.isNullOrBlank() && email != null && nickname != null

        if (isUserLoggedIn) {
            uiState = uiState.copy(
                emailInput = email!!, // 널 아님
                nicknameInput = nickname!!, // 널 아님
                isLoggedIn = true
            )
        }
    }

    // UI 입력 업데이트 함수
    fun updateEmail(newEmail: String) {
        uiState = uiState.copy(emailInput = newEmail)
    }

    fun updatePassword(newPassword: String) {
        uiState = uiState.copy(passwordInput = newPassword)
    }

    fun updateNickname(newNickname: String) {
        uiState = uiState.copy(nicknameInput = newNickname)
    }


    // ----------------------------------------------------
    // 1. 로그인
    fun onLoginClicked() {
        if (uiState.emailInput.isBlank() || uiState.passwordInput.isBlank()) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("Please enter your email and password"))
            }
            return
        }

        val userEmailUsedForLogin = uiState.emailInput
        val passwordEntered = uiState.passwordInput

        uiState = uiState.copy(isLoginLoading = true, loginError = null)

        viewModelScope.launch {
            val request = LoginRequest(
                userId = userEmailUsedForLogin,
                password = uiState.passwordInput
            )

            authRepository.login(request).onSuccess { response ->
                // 로그인 성공 하면 토큰과 사용자 정보를 로컬 저장소에 저장
                TokenManager.saveTokensAndUser(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    nickname = response.nickname,
                    userId = response.id
                    )

                // UI State 업데이트 (로그인 상태를 true로 설정)
                uiState = uiState.copy(
                    emailInput = userEmailUsedForLogin,
                    nicknameInput = response.nickname,
                    passwordInput = passwordEntered,
                    isLoggedIn = true
                )

                _events.send(AuthUiEvent.NavigateToLoginSuccess)

            }.onFailure { e ->
                val errorMessage = e.message ?: "Login failed due to an unknown error."
                _events.send(AuthUiEvent.ShowError(errorMessage))

                // 로그인 실패 시, 상태를 false로 유지
                uiState = uiState.copy(isLoginLoading = false, isLoggedIn = false)
            }

            uiState = uiState.copy(isLoginLoading = false)
        }
    }

    // ----------------------------------------------------
    // 2. 회원가입 로직 (실제 API 호출)
    fun onSignUpClicked() {
        if (uiState.emailInput.isBlank() || uiState.passwordInput.isBlank() || uiState.nicknameInput.isBlank() || uiState.passwordInput.length < 6) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("Please fill in all fields (min 6 chars for password)."))
            }
            return
        }

        val passwordEntered = uiState.passwordInput // 비밀번호 값 보존

        uiState = uiState.copy(isLoginLoading = true, loginError = null)
        viewModelScope.launch {
            val request = SignUpRequest(
                userId = uiState.emailInput,
                password = uiState.passwordInput,
                nickname = uiState.nicknameInput
            )

            authRepository.signUp(request).onSuccess { response ->
                // 회원가입 성공 시: 사용자 정보만 로컬 저장소에 저장
                TokenManager.saveUser(
                    email = response.userId,
                    nickname = response.nickname
                )

                // UI State 업데이트
                uiState = uiState.copy(
                    emailInput = response.userId,
                    nicknameInput = response.nickname,
                    passwordInput = passwordEntered // 비밀번호 보존
                )

                _events.send(AuthUiEvent.NavigateToLogin)

            }.onFailure { e ->
                val errorMessage = e.message ?: "Sign up failed due to an unknown error."
                _events.send(AuthUiEvent.ShowError(errorMessage))
            }

            uiState = uiState.copy(isLoginLoading = false)
        }
    }

    // ----------------------------------------------------
    // 3. 네비게이션 헬퍼
    // ----------------------------------------------------
    fun navigateToSignUpScreen() {
        viewModelScope.launch { _events.send(AuthUiEvent.NavigateToSignUp) }
    }
    fun navigateToLoginScreen() {
        viewModelScope.launch { _events.send(AuthUiEvent.NavigateToLogin) }
    }

    // 4. 로그아웃 로직 (로컬 삭제만 구현)
    fun onLogoutClicked() {
        viewModelScope.launch {
            TokenManager.clearTokens()
            // 로그아웃 시 모든 상태 초기화
            uiState = AuthUiState()
        }
    }

    // 5. 입력 필드 초기화 (화면 이동 시 사용)
    fun clearAuthInputFields() {
        uiState = uiState.copy(
            emailInput = "",
            passwordInput = "",
            nicknameInput = ""
        )
    }
}