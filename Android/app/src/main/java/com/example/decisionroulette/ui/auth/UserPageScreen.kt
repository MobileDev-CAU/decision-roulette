package com.example.decisionroulette.ui.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyPageScreen(
    onLogout: () -> Unit,
    onNavigateToEdit: () -> Unit,
) {
    // 더미 데이터
    val nickname = "Nickname"
    val userId = "cjdjidjfj15"
    val userName = "홍길동"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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

        Text(nickname, fontSize = 28.sp, modifier = Modifier.padding(top = 8.dp, bottom = 48.dp))

        MyPageInfoField("ID", userId, isEditable = false)
        MyPageInfoField("Password", "********", isEditable = false)
        MyPageInfoField("Name", userName, isEditable = false)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNavigateToEdit) {
                Text("modify", color = Color.Gray)
            }

            TextButton(onClick = onLogout) {
                Text("logout", color = Color.Black, fontSize = 16.sp)
                Icon(
                    Icons.AutoMirrored.Filled.ArrowRightAlt,
                    contentDescription = "Logout",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}

@Composable
fun MyPageInfoField(label: String, value: String, isEditable: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(value, fontSize = 16.sp)
            }
        }
    }
}