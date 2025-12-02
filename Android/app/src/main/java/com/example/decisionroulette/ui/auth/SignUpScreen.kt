package com.example.decisionroulette.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest


@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    // 💡 Hilt/Koin 등을 사용하지 않는 간단한 예시에서는 기본값으로 viewModel()을 사용합니다.
    viewModel: AuthViewModel = viewModel()
) {
    val state = viewModel.uiState

    // 💡 일회성 이벤트(AuthUiEvent) 처리
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                // 회원가입 성공 시 로그인 화면으로 이동
                AuthUiEvent.NavigateToLogin -> onNavigateToLogin()
                is AuthUiEvent.ShowError -> {
                    println("Sign Up Error: ${event.message}")
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", fontSize = 32.sp, modifier = Modifier.padding(bottom = 40.dp))

        // 1. 이메일 입력 필드
        OutlinedTextField(
            value = state.emailInput,
            onValueChange = viewModel::updateEmail,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        // 2. 닉네임 입력 필드
        OutlinedTextField(
            value = state.nicknameInput,
            onValueChange = viewModel::updateNickname,
            label = { Text("Nickname") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        // 3. 비밀번호 입력 필드
        OutlinedTextField(
            value = state.passwordInput,
            onValueChange = viewModel::updatePassword,
            label = { Text("Password (Min 6 characters)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            singleLine = true
        )

        // 4. 회원가입 버튼
        Button(
            onClick = viewModel::onSignUpClicked,
            enabled = !state.isLoginLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (state.isLoginLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                // 폰트가 정의되지 않았을 수 있으므로 기본 텍스트 스타일을 사용합니다.
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. 로그인 화면으로 돌아가기 버튼
        TextButton(onClick = viewModel::navigateToLoginScreen) {
            // 폰트가 정의되지 않았을 수 있으므로 기본 텍스트 스타일을 사용합니다.
            Text("Already have an account? Login")
        }
    }
}