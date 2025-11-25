package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BackButton(  onClick: () -> Unit,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Row가 화면 너비를 모두 차지하도록 설정
            .padding(top = 16.dp), // 상단 패딩 추가 (선택 사항)
        verticalAlignment = Alignment.CenterVertically // 텍스트와 버튼을 수직 중앙 정렬
    ) {
        // 1. ⬅️ Back Button (왼쪽에 붙음)
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Black),

            contentPadding = PaddingValues(0.dp), // 텍스트 크기 때문에 패딩 0
            modifier = Modifier.size(width = 40.dp, height = 40.dp) // 버튼 크기 조정
        ) {
            // 아이콘 대신 텍스트를 사용하는 경우
            Text(
                text = "<",
                fontSize = 20.sp // 텍스트 크기 조정
            )

        }

        // 2. 📝 Title Text (가운데 정렬)
        Text(
            text = "Create roulette",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 36.sp,
            textAlign = TextAlign.Center, // 텍스트 자체를 중앙 정렬
            modifier = Modifier
                .weight(1f) // 남은 공간을 모두 차지
                .padding(end = 48.dp) // 버튼 크기만큼 오른쪽 여백을 주어 시각적 중앙에 맞춤
        )
    }
}