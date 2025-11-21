package com.example.decisionroulette.ui.roulette

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.cos
import kotlin.math.sin

// 룰렛 색상 팔레트
val RouletteColors = listOf(
    Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6),
    Color(0xFFFFD54F), Color(0xFFBA68C8), Color(0xFF4DB6AC)
)

@Composable
fun RouletteScreen(
    viewModel: RouletteViewModel = viewModel() // 뷰모델 주입
) {
    val uiState by viewModel.uiState.collectAsState()

    // 애니메이션 상태 (회전 각도)
    val rotation = remember { Animatable(0f) }

    // 스핀 로직: uiState.isSpinning이 true가 되면 돔
    LaunchedEffect(uiState.isSpinning) {
        if (uiState.isSpinning) {
            // 랜덤한 바퀴 수 + 결과 위치 계산 로직은 나중에 정교화
            // 일단은 시각적으로 5바퀴(360*5) + 랜덤 각도로 돌림
            val targetAngle = 360f * 5 + (0..360).random()

            rotation.animateTo(
                targetValue = targetAngle,
                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
            )
            // 애니메이션 끝나면 뷰모델에 알림 (스핀 종료)
            viewModel.onSpinFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 상단 Title & Edit Icon
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        ) {
            Text(
                text = uiState.title, // "저녁 메뉴"
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 2. 사용자 이전 선택 TOP 3
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "사용자 이전 선택 TOP3", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            uiState.top3Keywords.forEach { keyword ->
                Text(text = keyword, fontSize = 16.sp, modifier = Modifier.padding(2.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. 룰렛 (Wheel)
        Box(
            contentAlignment = Alignment.Center
        ) {
            // 돌아가는 원판
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .rotate(rotation.value) // 애니메이션 값만큼 회전
            ) {
                val items = uiState.items
                if (items.isNotEmpty()) {
                    val sweepAngle = 360f / items.size

                    items.forEachIndexed { index, item ->
                        drawArc(
                            color = RouletteColors[index % RouletteColors.size],
                            startAngle = index * sweepAngle - 90f, // -90도는 12시 방향부터 시작하려고
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                        // 텍스트 그리기는 복잡해서 일단 생략
                    }
                }
            }

            // 테두리
            Canvas(modifier = Modifier.size(300.dp)) {
                drawCircle(
                    color = Color.Black,
                    radius = size.width / 2,
                    style = Stroke(width = 8.dp.toPx())
                )
            }

            // 중앙 START 버튼 (화살표 역할 겸용)
            Button(
                onClick = { viewModel.startSpin() }, // 클릭 시 스핀 시작
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .size(80.dp)
                    .border(4.dp, Color.Black, CircleShape)
            ) {
                Text("start", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            // 12시 방향 화살표 (고정된 핀)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Pointer",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(50.dp)
                    .offset(y = (-15).dp) // 위치 살짝 조정
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}