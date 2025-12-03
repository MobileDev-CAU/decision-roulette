package com.example.decisionroulette.ui.optioncreate.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.decisionroulette.ui.theme.Galmuri

// 🎨 디자인 컬러 (갈색)
private val CustomBrown = Color(0xFF685C57)
private val LightBrownBg = Color(0xFFEFEBE9) // 연한 갈색 배경

@Composable
fun AiRecommendationDialog(
    recommendations: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    // 사용자가 선택한 항목들을 담을 리스트
    val selectedItems = remember { mutableStateListOf<String>() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp) // 너무 길어지지 않게 제한
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 타이틀 (갈색 + 폰트)
                Text(
                    text = "AI Recommendations",
                    fontSize = 20.sp,
                    fontFamily = Galmuri,
                    fontWeight = FontWeight.Bold,
                    color = CustomBrown // 🔥 갈색 적용
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select items to add",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = Galmuri
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 추천 리스트 (체크박스)
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recommendations) { item ->
                        val isSelected = selectedItems.contains(item)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) {
                                        selectedItems.remove(item)
                                    } else {
                                        selectedItems.add(item)
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) selectedItems.add(item)
                                    else selectedItems.remove(item)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CustomBrown, // 🔥 체크 시 갈색
                                    uncheckedColor = Color.Gray,
                                    checkmarkColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                fontFamily = Galmuri,
                                color = if (isSelected) CustomBrown else Color.Black, // 선택 시 글자색 변경
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 취소 버튼 (연한 갈색)
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBrownBg,
                            contentColor = CustomBrown
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontSize = 16.sp, fontFamily = Galmuri, fontWeight = FontWeight.Bold)
                    }

                    // 추가 버튼 (진한 갈색)
                    Button(
                        onClick = { onConfirm(selectedItems) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomBrown,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add", fontSize = 16.sp, fontFamily = Galmuri, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}