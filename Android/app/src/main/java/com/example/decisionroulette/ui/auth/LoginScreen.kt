package com.example.decisionroulette.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.theme.Galmuri
import kotlinx.coroutines.flow.collectLatest

// 🎨 디자인 컬러 (갈색)
private val CustomBrown = Color(0xFF685C57)

@Composable
fun LoginScreen(
    onNavigateToLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AuthUiEvent.NavigateToLoginSuccess -> onNavigateToLoginSuccess()
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
        // 타이틀 (갈색 + 폰트)
        Text(
            text = "Login",
            fontSize = 32.sp,
            fontFamily = Galmuri,
            fontWeight = FontWeight.Bold,
            color = CustomBrown,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // 이메일 입력 필드
        OutlinedTextField(
            value = state.emailInput,
            onValueChange = viewModel::updateEmail,
            label = { Text("Email", fontFamily = Galmuri) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CustomBrown,
                focusedLabelColor = CustomBrown,
                cursorColor = CustomBrown
            )
        )

        // 비밀번호 입력 필드
        OutlinedTextField(
            value = state.passwordInput,
            onValueChange = viewModel::updatePassword,
            label = { Text("Password", fontFamily = Galmuri) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CustomBrown,
                focusedLabelColor = CustomBrown,
                cursorColor = CustomBrown
            )
        )

        // 로그인 버튼 (갈색 배경)
        Button(
            onClick = viewModel::onLoginClicked,
            enabled = !state.isLoginLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomBrown,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray
            )
        ) {
            if (state.isLoginLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Text("Login", fontFamily = Galmuri, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 회원가입 링크 (갈색 텍스트)
        TextButton(
            onClick = viewModel::navigateToSignUpScreen,
            colors = ButtonDefaults.textButtonColors(contentColor = CustomBrown)
        ) {
            Text("Join membership", fontFamily = Galmuri)
        }
    }
}