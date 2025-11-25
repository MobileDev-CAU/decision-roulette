package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.ui.theme.Galmuri

// 검정색 테두리 있는 버튼 컴포넌트 재사용
@Composable
fun BlackBorder (

    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp, // 평상시 높이
            pressedElevation = 2.dp  // 눌렀을 때 낮아지는 높이 (눌리는 효과)
        ),
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.Black),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontFamily = Galmuri
        )
    }
}