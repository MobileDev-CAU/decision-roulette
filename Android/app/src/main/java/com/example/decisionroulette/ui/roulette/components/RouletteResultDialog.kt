package com.example.decisionroulette.ui.roulette.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.decisionroulette.ui.theme.Galmuri


val MainBrown = Color(0xFF685C57)
val LightBrown = Color(0xFFD7CCC8)
val BackgroundWhite = Color(0xFFFDFBF7)

@Composable
fun RouletteResultDialog(
    resultName: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onVote: () -> Unit,
    onFinalConfirm: (String, Boolean) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var manualInputText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 상단 타이틀 (고정)
                Text(
                    text = "🎉 Result",
                    fontSize = 24.sp,
                    fontFamily = Galmuri,
                    fontWeight = FontWeight.Bold,
                    color = MainBrown
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 결과 표시 (원형 테두리)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = MainBrown,
                            style = Stroke(
                                width = 6f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Today's Pick",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontFamily = Galmuri
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resultName,
                            fontSize = 32.sp,
                            fontFamily = Galmuri,
                            fontWeight = FontWeight.ExtraBold,
                            color = MainBrown,
                            textAlign = TextAlign.Center,
                            lineHeight = 40.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 하단 버튼 영역 (Step에 따라 변경)
                when (step) {
                    1 -> Step1Buttons(
                        onConfirm = { step = 2 },
                        onRetry = onRetry,
                        onVote = onVote
                    )
                    2 -> Step2Buttons(
                        onYes = { onFinalConfirm(resultName, true) },
                        onNo = { step = 3 }
                    )
                    3 -> Step3Input(
                        text = manualInputText,
                        onValueChange = { manualInputText = it },
                        onConfirm = {
                            if (manualInputText.isNotBlank()) {
                                onFinalConfirm(manualInputText, false)
                            }
                        }
                    )
                }
            }
        }
    }
}

//  결과 확인 단계 버튼들
@Composable
fun Step1Buttons(
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onVote: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 확정 버튼 (가장 강조)
        PrimaryButton(
            text = "Confirm Selection",
            icon = Icons.Default.Check,
            onClick = onConfirm
        )

        // 보조 버튼들 (나란히 배치)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SecondaryButton(
                text = "Retry",
                icon = Icons.Default.Refresh,
                modifier = Modifier.weight(1f),
                onClick = onRetry
            )
            SecondaryButton(
                text = "Vote",
                icon = Icons.Default.Share, // 투표 아이콘 적절한 걸로 교체 가능
                modifier = Modifier.weight(1f),
                onClick = onVote
            )
        }
    }
}

// 만족 여부 확인 버튼들
@Composable
fun Step2Buttons(onYes: () -> Unit, onNo: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Are you satisfied?",
            fontSize = 18.sp,
            fontFamily = Galmuri,
            fontWeight = FontWeight.Bold,
            color = MainBrown
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryButton(
                text = "Yes!",
                modifier = Modifier.weight(1f),
                onClick = onYes
            )
            SecondaryButton(
                text = "No...",
                modifier = Modifier.weight(1f),
                onClick = onNo
            )
        }
    }
}

// 직접 입력 필드
@Composable
fun Step3Input(
    text: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "What is your final choice?",
            fontSize = 16.sp,
            fontFamily = Galmuri,
            fontWeight = FontWeight.Bold,
            color = MainBrown
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            placeholder = { Text("Enter your choice", fontFamily = Galmuri, fontSize = 14.sp) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainBrown,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = MainBrown,
                focusedTextColor = MainBrown
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            text = "Confirm",
            onClick = onConfirm
        )
    }
}

// 공통 버튼 컴포넌트
@Composable
fun PrimaryButton(
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MainBrown),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, fontSize = 16.sp, fontFamily = Galmuri, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

// 공통 버튼 컴포넌트 - 보조
@Composable
fun SecondaryButton(
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MainBrown),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MainBrown),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = MainBrown, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(text = text, fontSize = 14.sp, fontFamily = Galmuri, fontWeight = FontWeight.Bold)
    }
}