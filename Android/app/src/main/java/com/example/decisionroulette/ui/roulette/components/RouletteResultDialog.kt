package com.example.decisionroulette.ui.roulette.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun RouletteResultDialog(
    resultName: String,     // 결과 (예: "치킨")
    onDismiss: () -> Unit,  // 팝업 닫기 (배경 클릭 등)
    onRetry: () -> Unit,    // 다시 돌리기
    onVote: () -> Unit,     // 투표 올리기
    onFinalConfirm: (String) -> Unit // 최종 선택한 메뉴를 서버로 보냄
) {
    var step by remember { mutableIntStateOf(1) }
    var manualInputText by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        // 흰색 배경 카드
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 상단 타이틀
                Text(
                    text = "오늘의 메뉴",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 2. 중앙 점선 원 + 텍스트
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(200.dp)
                ) {
                    // (A) 점선 원 그리기
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.Black,
                            style = Stroke(
                                width = 8f, // 선 두께
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f) // 점선 패턴
                            )
                        )
                    }
                    Text(
                        text = resultName,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                when (step) {
                    1 -> {
                        // [Step 1] 기본 버튼 3개
                        ResultButton(text = "선택 확정하기", onClick = { step = 2 }) // 누르면 2단계로
                        Spacer(modifier = Modifier.height(12.dp))
                        ResultButton(text = "룰렛 다시 돌리기", onClick = onRetry)
                        Spacer(modifier = Modifier.height(12.dp))
                        ResultButton(text = "유저 투표 올리기", onClick = onVote)
                    }

                    2 -> {
                        // [Step 2] 결과를 따르시겠습니까? (예/아니오)
                        Text(
                            text = "룰렛 결과를 따르시겠습니까?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // [예] -> 룰렛 결과(resultName) 그대로 확정
                            Button(
                                onClick = { onFinalConfirm(resultName) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                                modifier = Modifier.weight(1f).height(50.dp)
                            ) {
                                Text("예", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            // [아니오] -> 3단계(직접 입력)로 이동
                            Button(
                                onClick = { step = 3 },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                modifier = Modifier.weight(1f).height(50.dp)
                            ) {
                                Text("아니오", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    3 -> {
                        // [Step 3] 최종 결정 직접 입력
                        Text(
                            text = "최종 결정은 무엇인가요?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // 입력창 (TextField)
                        OutlinedTextField(
                            value = manualInputText,
                            onValueChange = { manualInputText = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 확인 버튼 -> 사용자가 입력한 값(manualInputText)으로 확정
                        Button(
                            onClick = {
                                if (manualInputText.isNotBlank()) {
                                    onFinalConfirm(manualInputText)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 3. 버튼 3개 (회색 둥근 버튼들)
//                ResultButton(text = "선택 확정하기", onClick = onConfirm)
//                Spacer(modifier = Modifier.height(12.dp))
//                ResultButton(text = "룰렛 다시 돌리기", onClick = onRetry)
//                Spacer(modifier = Modifier.height(12.dp))
//                ResultButton(text = "유저 투표 올리기", onClick = onVote)
            }
        }
    }
}

// 버튼 스타일 통일을 위한 컴포넌트
@Composable
fun ResultButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)), // 연한 회색
        shape = RoundedCornerShape(25.dp) // 완전 둥글게
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}