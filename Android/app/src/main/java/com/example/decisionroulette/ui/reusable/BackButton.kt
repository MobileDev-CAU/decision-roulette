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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.ui.theme.Galmuri

@Composable
fun BackButton(  onClick: () -> Unit,

) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Row가 화면 너비를 모두 차지하도록 설정
            .padding(top = 16.dp), // 상단 패딩 추가 (선택 사항)
        verticalAlignment = Alignment.CenterVertically // 텍스트와 버튼을 수직 중앙 정렬
    ) {
        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.Black // 텍스트 색상만 지정
            ),
            contentPadding = PaddingValues(0.dp), // 패딩 제거
            modifier = Modifier.size(width = 60.dp, height = 60.dp) // 터치 영역 확보
        ) {
            Text(
                text = "<",
                fontSize = 40.sp,
                fontFamily = Galmuri,
                // 텍스트 위치 보정이 필요할 수 있습니다.
//                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        // 2. 📝 Title Text (가운데 정렬)
        // Modifier.weight(1f)를 사용하여 남은 공간을 차지하게 하고, Text의 Modifier로 중앙 정렬합니다.
        Text(
            text = "Create roulette",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 28.sp,
            textAlign = TextAlign.Center, // 텍스트 자체를 중앙 정렬
            modifier = Modifier
                .weight(1f) // 남은 공간을 모두 차지
                .padding(end = 48.dp) // 버튼 크기만큼 오른쪽 여백을 주어 시각적 중앙에 맞춤
        )
    }
}