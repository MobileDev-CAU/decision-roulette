package com.example.decisionroulette.ui.roulette

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RouletteWheel(
    items: List<String>,
    rotationValue: Float, // 애니메이션 각도를 밖에서 받아옵니다 (State Hoisting)
    onStartClick: () -> Unit // 클릭 이벤트를 밖으로 전달합니다
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        // 3-1. 돌아가는 원판
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .rotate(rotationValue)
        ) {
            if (items.isNotEmpty()) {
                val sweepAngle = 360f / items.size
                items.forEachIndexed { index, _ ->
                    drawArc(
                        color = RouletteColors[index % RouletteColors.size],
                        startAngle = index * sweepAngle - 90f,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                }
            }
        }

        // 3-2. 검은색 테두리
        Canvas(modifier = Modifier.size(300.dp)) {
            drawCircle(
                color = Color.Black,
                radius = size.width / 2,
                style = Stroke(width = 8.dp.toPx())
            )
        }

        // 3-3. START 버튼
        Button(
            onClick = onStartClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .size(80.dp)
                .border(4.dp, Color.Black, CircleShape)
        ) {
            Text("start", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        // 3-4. 화살표 핀
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Pointer",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(50.dp)
                .offset(y = (-15).dp)
        )
    }
}