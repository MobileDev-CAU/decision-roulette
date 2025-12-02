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
import com.example.decisionroulette.ui.theme.Galmuri
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    onNavigateToUserPage: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: AuthViewModel
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {

                AuthUiEvent.NavigateToUserPage -> onNavigateToUserPage()
                AuthUiEvent.NavigateToSignUp -> onNavigateToSignUp()
                is AuthUiEvent.ShowError -> {
                    println("Login Error: ${event.message}")
                    // TODO: Snackbar 또는 Toast로 오류 메시지 표시
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
        Text("Login", fontSize = 32.sp, modifier = Modifier.padding(bottom = 48.dp))

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
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), // 비밀번호 숨기기
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        // 로그인 버튼
        Button(
            onClick = viewModel::onLoginClicked,
            enabled = !state.isLoginLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (state.isLoginLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 3.dp
                )
            } else {
                Text("Login", fontFamily = Galmuri)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // 회원가입 링크
        TextButton(onClick = viewModel::navigateToSignUpScreen) {
            Text(
                "Join membership", fontFamily = Galmuri)
        }
    }
}