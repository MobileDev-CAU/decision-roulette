package com.example.decisionroulette.ui.roulette.components

import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.data.RouletteItem
import com.example.decisionroulette.ui.theme.Galmuri
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// 🎨 디자인 컬러 정의
val CustomBrown = Color(0xFF685C57)

// 🎨 빈티지 파스텔톤 룰렛 색상
val RouletteColors = listOf(
    Color(0xFFD7CCC8), // 연한 갈색
    Color(0xFFFFCCBC), // 살구색
    Color(0xFFC5E1A5), // 연두색
    Color(0xFFFFF59D), // 연노랑
    Color(0xFFB39DDB), // 연보라
    Color(0xFF80CBC4)  // 민트
)

@Composable
fun RouletteWheel(
    items: List<RouletteItem>,
    rotationValue: Float,
    onStartClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        // 3-1. 돌아가는 원판
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .rotate(rotationValue)
        ) {
            if (items.isNotEmpty()) {
                val totalWeight = items.map { it.weight }.sum()
                var currentStartAngle = -90f

                items.forEachIndexed { index, item ->
                    val sweepAngle = (item.weight / totalWeight) * 360f

                    // 색상 순환 로직
                    val color = if (items.size % 2 != 0 && index == items.lastIndex) {
                        // 홀수 개수일 때 마지막은 첫 번째와 다른 색(3번째 색 등) 사용
                        RouletteColors[2 % RouletteColors.size]
                    } else {
                        RouletteColors[index % RouletteColors.size]
                    }

                    // (A) 부채꼴 그리기
                    drawArc(
                        color = color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )

                    // (B) 글자 그리기
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
//                            color = android.graphics.Color.BLACK
                            textSize = 40f
                            textAlign = Paint.Align.CENTER
                            typeface = Typeface.DEFAULT_BOLD
                        }

                        val textAngleRad = (currentStartAngle + sweepAngle / 2) * (PI / 180f)
                        val x = (center.x + size.width / 2 * 0.6f * cos(textAngleRad)).toFloat()
                        val y = (center.y + size.width / 2 * 0.6f * sin(textAngleRad)).toFloat()

                        canvas.save()
                        canvas.rotate(currentStartAngle + sweepAngle / 2 + 90, x, y)
                        canvas.nativeCanvas.drawText(item.name, x, y + 15f, paint)
                        canvas.restore()
                    }
                    currentStartAngle += sweepAngle
                }
            }
        }

        // 3-2. 테두리 (갈색)
        Canvas(modifier = Modifier.size(300.dp)) {
            drawCircle(
                color = CustomBrown,
                radius = size.width / 2,
                style = Stroke(width = 4.dp.toPx()) // 테두리 조금 더 두껍게
            )
        }

        // 3-3. START 버튼
        Button(
            onClick = onStartClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(80.dp)
                .border(3.dp, CustomBrown, CircleShape) // 갈색 테두리
        ) {
            Text(
                text = "START",
                color = CustomBrown, // 갈색 텍스트
                fontWeight = FontWeight.Bold,
                fontFamily = Galmuri,
                fontSize = 18.sp
            )
        }

        // 3-4. 화살표 핀 (둥근 삼각형)
        Canvas(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(45.dp)
                .offset(y = (-15).dp)
        ) {
            val trianglePath = Path().apply {
                moveTo(size.width / 2f, size.height)
                lineTo(0f, 0f)
                lineTo(size.width, 0f)
                close()
            }
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    isAntiAlias = true
                    pathEffect = CornerPathEffect(10f) // 둥근 모서리 효과
                }

                // 내부 채우기 (흰색)
                paint.style = Paint.Style.FILL
                paint.color = android.graphics.Color.WHITE
                canvas.nativeCanvas.drawPath(trianglePath.asAndroidPath(), paint)

                // 테두리 그리기 (갈색)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3.dp.toPx()
                paint.color = android.graphics.Color.parseColor("#685C57")
                canvas.nativeCanvas.drawPath(trianglePath.asAndroidPath(), paint)
            }
        }
    }
}