package com.example.decisionroulette.ui.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.* import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.decisionroulette.ui.auth.AuthViewModel
import com.example.decisionroulette.Routes // Routes 객체 Import 필수

@Composable
fun MyPageScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val uiState = authViewModel.uiState


    LaunchedEffect(uiState.isLoggedIn) {
        if (!uiState.isLoggedIn) {
            // 로그인 화면으로 이동시키고, 이전 백 스택을 모두 지워버립니다.
            navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val nickname = uiState.nicknameInput.ifBlank { "Guest" }
    val userId = uiState.emailInput.ifBlank { "N/A" }
    val actualPassword = uiState.passwordInput
    // 로그인 상태일 때만 UI 렌더링
    if (uiState.isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 프로필 이미지
            Image(
                imageVector = Icons.Filled.Person,
                contentDescription = "User Profile Icon",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .padding(16.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
            )

            // 닉네임 표시
            Text(nickname, fontSize = 28.sp, modifier = Modifier.padding(top = 8.dp, bottom = 48.dp))

            // ID (이메일) 표시
            MyPageInfoField("ID (Email)", userId, isEditable = false)

            // 비밀번호 표시 및 토글 기능
            MyPagePasswordField(actualPassword = actualPassword)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = authViewModel::onLogoutClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout", fontSize = 18.sp)
            }
        }
    }
}

// -------------------------------------------------------------------------
// 보조 Composable: MyPagePasswordField와 MyPageInfoField는 이전 코드를 유지
// -------------------------------------------------------------------------

@Composable
fun MyPagePasswordField(actualPassword: String) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isPasswordBlank = actualPassword.isBlank()

    val displayPassword = when {
        isPasswordBlank -> "********" // 비밀번호가 비어있으면 그냥 마스킹
        isPasswordVisible -> actualPassword // 토글 시 실제 값 표시
        else -> "********" // 토글 전 마스킹
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {
        Text("Password", fontSize = 14.sp, color = Color.Gray)
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    displayPassword,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .clickable {
                            isPasswordVisible = !isPasswordVisible
                        },
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MyPageInfoField(label: String, value: String, isEditable: Boolean) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(value, fontSize = 16.sp)
            }
        }
    }
}