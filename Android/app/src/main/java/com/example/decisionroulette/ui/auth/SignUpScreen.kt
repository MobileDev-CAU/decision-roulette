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
    viewModel: AuthViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AuthUiEvent.NavigateToLogin -> onNavigateToLogin()
                is AuthUiEvent.ShowError -> {
                    println("Sign Up Error: ${event.message}")
                    // TODO: Snackbar 또는 Toast로 오류 메시지 표시
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", fontSize = 32.sp, modifier = Modifier.padding(bottom = 48.dp))

        // 이메일 입력 필드
        // 이메일 입력 필드
        OutlinedTextField(
            value = state.emailInput,
            onValueChange = viewModel::updateEmail,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

       // 비밀번호 입력 필드
        OutlinedTextField(
            value = state.passwordInput,
            onValueChange = viewModel::updatePassword,
            label = { Text("Password (6 or more digits)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        // 회원가입 버튼
        Button(
            onClick = viewModel::onSignUpClicked,
            enabled = !state.isLoginLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (state.isLoginLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Sign Up Completed")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 로그인 화면으로 돌아가기
        TextButton(onClick = viewModel::navigateToLoginScreen) {
            Text("Login")
        }
    }
}