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

@androidx.compose.runtime.Composable
fun AiRecommendationDialog(
    recommendations: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    // 사용자가 선택한 항목들을 담을 리스트
    val selectedItems =
        _root_ide_package_.androidx.compose.runtime.remember { _root_ide_package_.androidx.compose.runtime.mutableStateListOf<String>() }

    _root_ide_package_.androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        _root_ide_package_.androidx.compose.material3.Card(
            shape = _root_ide_package_.androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = _root_ide_package_.androidx.compose.material3.CardDefaults.cardColors(
                containerColor = _root_ide_package_.androidx.compose.ui.graphics.Color.Companion.White
            ),
            modifier = _root_ide_package_.androidx.compose.ui.Modifier.Companion
                .fillMaxWidth()
                .heightIn(max = 500.dp) // 너무 길어지지 않게 제한
                .padding(16.dp)
        ) {
            _root_ide_package_.androidx.compose.foundation.layout.Column(
                modifier = _root_ide_package_.androidx.compose.ui.Modifier.Companion.padding(24.dp),
                horizontalAlignment = _root_ide_package_.androidx.compose.ui.Alignment.Companion.CenterHorizontally
            ) {
                _root_ide_package_.androidx.compose.material3.Text(
                    text = "AI Recommendations",
                    fontSize = 20.sp,
                    fontFamily = Galmuri,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedItems.contains(item)) {
                                        selectedItems.remove(item)
                                    } else {
                                        selectedItems.add(item)
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedItems.contains(item),
                                onCheckedChange = { checked ->
                                    if (checked) selectedItems.add(item)
                                    else selectedItems.remove(item)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Black,
                                    uncheckedColor = Color.Gray,
                                    checkmarkColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                fontFamily = Galmuri
                            )
                        }
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 취소 버튼
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = Color.Black, fontFamily = Galmuri)
                    }

                    // 추가 버튼
                    Button(
                        onClick = { onConfirm(selectedItems) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add", color = Color.White, fontFamily = Galmuri)
                    }
                }
            }
        }
    }
}