package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
fun BackButton(
    title: String,
    onClick: () -> Unit,
) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth() // Row가 화면 너비를 모두 차지하도록 설정
//            .padding(top = 80.dp),
//        verticalAlignment = Alignment.CenterVertically // 텍스트와 버튼을 수직 중앙 정렬
//    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(start = 0.dp),
            modifier = Modifier.size(width = 60.dp, height = 60.dp).align(Alignment.CenterStart)
        ) {
            Text(
                text = "<",
                fontSize = 25.sp,
                fontFamily = Galmuri,
            )
        }

        // 2. Title Text (가운데 정렬)
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 25.sp,
            textAlign = TextAlign.Center, // 텍스트 자체를 중앙 정렬
            modifier = Modifier.align(Alignment.Center)
        )
    }
}