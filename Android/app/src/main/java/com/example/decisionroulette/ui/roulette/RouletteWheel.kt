package com.example.decisionroulette.ui.roulette

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.decisionroulette.data.RouletteItem
import com.example.decisionroulette.ui.theme.Galmuri
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill

@Composable
fun RouletteWheel(
    items: List<RouletteItem>,
    rotationValue: Float, // 애니메이션 각도를 밖에서 받아옴
    onStartClick: () -> Unit // 클릭 이벤트를 밖으로 전달
) {
    Box(contentAlignment = Alignment.Center) {
        // 3-1. 돌아가는 원판
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .rotate(rotationValue)
        ) {
            if (items.isNotEmpty()) {
                // 1. 전체 가중치 합 구하기 (예: 0.4 + 0.3 + 0.3 = 1.0)
                val totalWeight = items.map { it.weight }.sum()

                // 2. 시작 각도 추적 변수 (12시 방향 -90도부터 시작)
                var currentStartAngle = -90f

                items.forEachIndexed { index, item ->
                    // 3. 내 지분에 따른 각도 계산 (360 * 비율)
                    val sweepAngle = (item.weight / totalWeight) * 360f

                    val radius = size.width / 2

                    // (A) 부채꼴 그리기
                    drawArc(
                        color = RouletteColors[index % RouletteColors.size],
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )

                    // (B) 글자 그리기
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 40f
                            textAlign = Paint.Align.CENTER
                            typeface = Typeface.DEFAULT_BOLD
                        }

                        // 글자는 부채꼴의 정중앙에 위치해야 함
                        val textAngleRad = (currentStartAngle + sweepAngle / 2) * (PI / 180f)
                        val x = (center.x + radius * 0.6f * cos(textAngleRad)).toFloat()
                        val y = (center.y + radius * 0.6f * sin(textAngleRad)).toFloat()

                        canvas.save()
                        canvas.rotate(
                            currentStartAngle + sweepAngle / 2 + 90, // 글자 회전 각도
                            x, y
                        )
                        canvas.nativeCanvas.drawText(item.name, x, y + 15f, paint)
                        canvas.restore()
                    }

                    // 4. 다음 아이템을 위해 시작 각도 업데이트 (누적)
                    currentStartAngle += sweepAngle
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
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .size(80.dp)
                .border(4.dp, Color.Black, CircleShape)
        ) {
            Text("start", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = Galmuri)
        }

        // 3-4. 화살표 핀
        Canvas(
            modifier = Modifier
                .align(Alignment.TopCenter) // 상단 중앙 정렬
                .size(35.dp) // 크기
                .offset(y = (-10).dp) // 위치 조정
        ) {
            // 1. 역삼각형 경로(모양) 정의
            val trianglePath = Path().apply {
                // 좌표: (x, y) 기준
                moveTo(size.width / 2f, size.height) // 하단 중앙 꼭짓점 (뾰족한 부분)
                lineTo(0f, 0f) // 상단 왼쪽 꼭짓점
                lineTo(size.width, 0f) // 상단 오른쪽 꼭짓점
                close() // 경로 닫기 (삼각형 완성)
            }

            // 2. 내부 채우기
            drawPath(
                path = trianglePath,
                color = Color.White, // 내부 색상
                style = Fill
            )

            // 3. 검정색 테두리 그리기
            drawPath(
                path = trianglePath,
                color = Color.Black, // 테두리 색상 (검정)
                style = Stroke(width = 3.dp.toPx()) // 테두리 두께 (5dp, 조절 가능)
            )
        }
    }
}