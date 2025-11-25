/*package com.example.decisionroulette.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// 🚨 화면 상태 정의
data class AuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val passwordConfirmInput: String = "", // ⬅️ 1. 비밀번호 확인 필드 추가
    val isLoginLoading: Boolean = false,
    val loginError: String? = null
)

// 🚨 이벤트 정의 (네비게이션 및 오류 처리)
sealed interface AuthUiEvent {
    object NavigateToUserPage : AuthUiEvent // ⬅️ 2. 로그인 성공 시 사용자 정보 페이지로 이동 이벤트
    object NavigateToSignUp : AuthUiEvent
    object NavigateToLogin : AuthUiEvent
    data class ShowError(val message: String) : AuthUiEvent
}

class AuthViewModel : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    private val _events = Channel<AuthUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun updateEmail(newEmail: String) {
        uiState = uiState.copy(emailInput = newEmail)
    }

    fun updatePassword(newPassword: String) {
        uiState = uiState.copy(passwordInput = newPassword)
    }

    // ⬅️ 3. 비밀번호 확인 필드 업데이트 함수 추가
    fun updatePasswordConfirm(newPasswordConfirm: String) {
        uiState = uiState.copy(passwordConfirmInput = newPasswordConfirm)
    }

    // ----------------------------------------------------
    // 1. 로그인 로직 (성공 시 NavigateToMyPage 이벤트 발행)
    fun onLoginClicked() {
        if (uiState.emailInput.isBlank() || uiState.passwordInput.isBlank()) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("이메일과 비밀번호를 입력해주세요."))
            }
            return
        }

        // 🚨 실제 로그인 처리 (서버 연동)
        uiState = uiState.copy(isLoginLoading = true, loginError = null)
        viewModelScope.launch {
            // TODO: 실제 서버 API 호출 로직 (예: Retrofit)
            kotlinx.coroutines.delay(1500) // 로딩 시뮬레이션

            if (uiState.emailInput == "test@a.com" && uiState.passwordInput == "1234") {
                // ⬅️ 4. 로그인 성공 시 사용자 정보 페이지로 이동
                _events.send(AuthUiEvent.NavigateToUserPage)
            } else {
                _events.send(AuthUiEvent.ShowError("이메일 또는 비밀번호가 올바르지 않습니다."))
            }
            uiState = uiState.copy(isLoginLoading = false)
        }
    }

    // 2. 회원가입 로직 (비밀번호 일치 여부 확인 추가)
    fun onSignUpClicked() {
        if (uiState.passwordInput.length < 6) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("비밀번호는 6자리 이상이어야 합니다."))
            }
            return
        }

        // ⬅️ 5. 비밀번호 일치 여부 확인
        if (uiState.passwordInput != uiState.passwordConfirmInput) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("비밀번호가 일치하지 않습니다."))
            }
            return
        }


        // 🚨 실제 회원가입 처리 (서버 연동)
        uiState = uiState.copy(isLoginLoading = true, loginError = null)
        viewModelScope.launch {
            // TODO: 실제 서버 API 호출 로직
            kotlinx.coroutines.delay(1500)

            // 성공했다고 가정 후 로그인 화면으로 복귀
            _events.send(AuthUiEvent.NavigateToLogin)
            uiState = uiState.copy(isLoginLoading = false)
        }
    }

    // 3. 네비게이션 헬퍼
    fun navigateToSignUpScreen() {
        viewModelScope.launch { _events.send(AuthUiEvent.NavigateToSignUp) }
    }
    fun navigateToLoginScreen() {
        viewModelScope.launch { _events.send(AuthUiEvent.NavigateToLogin) }
    }

    fun navigateToUserPageScreen() {
        viewModelScope.launch { _events.send(AuthUiEvent.NavigateToUserPage) }
    }
}*/
package com.example.decisionroulette.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay // ⬅️ delay 함수를 위해 필요


sealed interface AuthUiEvent {
    object NavigateToUserPage : AuthUiEvent // 로그인 성공 후 사용자 정보 페이지로 이동
    object NavigateToSignUp : AuthUiEvent
    object NavigateToLogin : AuthUiEvent
    data class ShowError(val message: String) : AuthUiEvent
}

class AuthViewModel : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    private val _events = Channel<AuthUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun updateEmail(newEmail: String) {
        uiState = uiState.copy(emailInput = newEmail)
    }

    fun updatePassword(newPassword: String) {
        uiState = uiState.copy(passwordInput = newPassword)
    }

    fun updatePasswordConfirm(newPasswordConfirm: String) {
        uiState = uiState.copy(passwordConfirmInput = newPasswordConfirm)
    }

    // ----------------------------------------------------
    // 1. 로그인 로직 (더미 데이터 기반)
    fun onLoginClicked() {
        if (uiState.emailInput.isBlank() || uiState.passwordInput.isBlank()) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("Please enter your email and password"))
            }
            return
        }

        uiState = uiState.copy(isLoginLoading = true, loginError = null)

        viewModelScope.launch {
            delay(1000)

            // 더미 로그인 성공 조건: 이메일=test@a.com, 비밀번호=1234
            if (uiState.emailInput == "test" && uiState.passwordInput == "1234") {

                // 로그인 성공 시, 사용자 정보 페이지로 이동 이벤트 발행
                _events.send(AuthUiEvent.NavigateToUserPage)

            } else {
                // 로그인 실패 시, 오류 메시지 이벤트 발행
                _events.send(AuthUiEvent.ShowError("The login information is invalid"))
            }

            uiState = uiState.copy(isLoginLoading = false)
        }
    }

    // 2. 회원가입 로직 (비밀번호 일치 여부 확인 포함)
    fun onSignUpClicked() {
        // 유효성 검사
        if (uiState.passwordInput.length < 6) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("Password must be at least 6 digits"))
            }
            return
        }

        // 비밀번호 일치 여부 확인
        if (uiState.passwordInput != uiState.passwordConfirmInput) {
            viewModelScope.launch {
                _events.send(AuthUiEvent.ShowError("Password does not match"))
            }
            return
        }

        // 실제 회원가입 처리 시뮬레이션
        uiState = uiState.copy(isLoginLoading = true, loginError = null)
        viewModelScope.launch {
            // TODO: 실제 서버 API 호출 로직
            delay(1500)

            // 성공했다고 가정 후 로그인 화면으로 복귀
            _events.send(AuthUiEvent.NavigateToLogin)
            uiState = uiState.copy(isLoginLoading = false)
        }
    }

    // 3. 네비게이션 헬퍼
    fun navigateToSignUpScreen() {
        viewModelScope.launch { _events.send(AuthUiEvent.NavigateToSignUp) }
    }
    fun navigateToLoginScreen() {
        viewModelScope.launch { _events.send(AuthUiEvent.NavigateToLogin) }
    }

}