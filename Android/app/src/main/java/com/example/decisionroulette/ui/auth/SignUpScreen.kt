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

private val CustomBrown = Color(0xFF685C57)

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

        Text(
            text = "Sign Up",
            fontSize = 32.sp,
            fontFamily = Galmuri,
            fontWeight = FontWeight.Bold,
            color = CustomBrown,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        //  이메일 입력 필드
        OutlinedTextField(
            value = state.emailInput,
            onValueChange = viewModel::updateEmail,
            label = { Text("ID", fontFamily = Galmuri) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CustomBrown,
                focusedLabelColor = CustomBrown,
                cursorColor = CustomBrown
            )
        )

        //  닉네임 입력 필드
        OutlinedTextField(
            value = state.nicknameInput,
            onValueChange = viewModel::updateNickname,
            label = { Text("Nickname", fontFamily = Galmuri) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CustomBrown,
                focusedLabelColor = CustomBrown,
                cursorColor = CustomBrown
            )
        )

        // 회원가입 버튼
        Button(
            onClick = viewModel::onSignUpClicked,
            enabled = !state.isLoginLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomBrown,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray
            )
        ) {
            if (state.isLoginLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Sign Up", fontFamily = Galmuri, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 로그인 화면 링크 버튼
        TextButton(
            onClick = viewModel::navigateToLoginScreen,
            colors = ButtonDefaults.textButtonColors(contentColor = CustomBrown)
        ) {
            Text("Already have an account? Login", fontFamily = Galmuri)
        }
    }
}