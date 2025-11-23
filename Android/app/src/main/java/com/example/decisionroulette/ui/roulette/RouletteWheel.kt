package com.example.decisionroulette.ui.roulette

import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.ui.roulette.RouletteColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RouletteWheel(
    items: List<String>,
    rotationValue: Float, // 애니메이션 각도를 밖에서 받아옴
    onStartClick: () -> Unit // 클릭 이벤트를 밖으로 전달
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
                val radius = size.width / 2

                // (A) 부채꼴 색상 그리기
                items.forEachIndexed { index, _ ->
                    drawArc(
                        color = RouletteColors[index % RouletteColors.size],
                        startAngle = index * sweepAngle - 90f,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                }

                // (B) 글자 그리기 (Native Canvas 사용)
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 40f // 글자 크기
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.DEFAULT_BOLD
                    }

                    items.forEachIndexed { index, item ->
                        // 각 조각의 중앙 각도 계산 (Radian)
                        // -90도를 해주는 이유는 12시 방향이 0도가 되게 맞추기 위함
                        val angleRad = (index * sweepAngle + sweepAngle / 2 - 90) * (PI / 180f)

                        // 글자가 위치할 좌표 계산 (중심에서 60% 지점)
                        val x = (center.x + radius * 0.6f * cos(angleRad)).toFloat()
                        val y = (center.y + radius * 0.6f * sin(angleRad)).toFloat()

                        // 텍스트 회전 (글자가 중심을 바라보게)
                        canvas.save()
                        canvas.rotate(
                            index * sweepAngle + sweepAngle / 2, // 회전 각도
                            x, y // 회전 기준점 (글자 위치)
                        )
                        canvas.nativeCanvas.drawText(item, x, y + 15f, paint) // y+15f는 수직 중앙 정렬 보정
                        canvas.restore()
                    }
                }
            }
        }

        // 3-2. 검은색 테두리
        Canvas(modifier = Modifier.size(300.dp)) {
            drawCircle(
                color = Color.Black,
                radius = size.width / 2,
                style = Stroke(width = 4.dp.toPx())
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