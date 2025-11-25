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
import com.example.decisionroulette.ui.roulette.components.RouletteResultDialog
import com.example.decisionroulette.ui.theme.Galmuri

// 룰렛 색상 팔레트
val RouletteColors = listOf(
    Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6),
    Color(0xFFFFD54F), Color(0xFFBA68C8), Color(0xFF4DB6AC)
)

@Composable
fun RouletteScreen(
    viewModel: RouletteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(uiState.isSpinning) {
        if (uiState.isSpinning) {
            // 뷰모델이 계산해준 정확한 각도만큼 더함
            // rotation.value (현재 위치) + uiState.targetRotation (추가 회전량)
            val finalTarget = rotation.value + uiState.targetRotation

            rotation.animateTo(
                targetValue = finalTarget,
                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
            )
            viewModel.onSpinFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 헤더 컴포넌트 호출
        RouletteHeader(title = uiState.title)

        ModeToggleSwitch(
            isVoteMode = uiState.isVoteMode,
            onToggle = { isVote -> viewModel.toggleMode(isVote) }
        )
        Spacer(modifier = Modifier.height(30.dp))

        // 2. 통계 박스 컴포넌트 호출
        Top3KeywordsBox(keywords = uiState.top3Keywords)

        Spacer(modifier = Modifier.weight(1f))

        // 3. 룰렛 휠 컴포넌트 호출
        // 핵심: 회전 값(rotation.value)과 클릭 이벤트(startSpin)를 파라미터로 넘깁니다.
        RouletteWheel(
            items = uiState.items,
            rotationValue = rotation.value,
            onStartClick = { viewModel.startSpin(rotation.value) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { viewModel.addDummyItem() }) {
            Text("테스트: 메뉴 추가하기 (+)")
        }
    }

    if (uiState.showResultDialog && uiState.spinResult != null) {
        RouletteResultDialog(
            resultName = uiState.spinResult!!,
            onDismiss = { viewModel.closeDialog() },
            onRetry = { viewModel.retrySpin() },
            onVote = { viewModel.uploadVote() },
            onFinalConfirm = { finalChoice ->
                viewModel.saveFinalChoice(finalChoice)
            }
        )
    }
}

@Composable
fun RouletteHeader(title: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
    ) {
        Text(
            text = title,
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
}

@Composable
fun ModeToggleSwitch(
    isVoteMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color(0xFFEEEEEE), RoundedCornerShape(50))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 기본 모드 버튼
        ToggleButton(
            text = "기본 (1/N)",
            isSelected = !isVoteMode,
            onClick = { onToggle(false) }
        )

        // 투표 반영 버튼
        ToggleButton(
            text = "투표 반영",
            isSelected = isVoteMode,
            onClick = { onToggle(true) }
        )
    }
}

@Composable
fun ToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.White else Color.Transparent,
            contentColor = if (isSelected) Color.Black else Color.Gray
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
        shape = RoundedCornerShape(50),
        modifier = Modifier.height(36.dp)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = Galmuri)
    }
}

@Composable
fun Top3KeywordsBox(keywords: List<String>) {
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

        keywords.forEach { keyword ->
            Text(text = keyword, fontSize = 16.sp, modifier = Modifier.padding(2.dp))
        }
    }
}