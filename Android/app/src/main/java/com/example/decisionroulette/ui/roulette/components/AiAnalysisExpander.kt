package com.example.decisionroulette.ui.roulette.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.api.roulette.AiAnalysisItem
import com.example.decisionroulette.ui.theme.Galmuri

//private val CustomBrown = Color(0xFF685C57)

@Composable
fun AiAnalysisExpander(
    analysisResult: List<AiAnalysisItem>,
    isLoading: Boolean = false
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE0E0E0), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨ AI Analysis Report",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Galmuri,
                color = CustomBrown // 🔥 갈색 적용
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = CustomBrown // 🔥 아이콘도 갈색
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                if (analysisResult.isEmpty()) {
                    Text(
                        text = "Analyzing roulette items...",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = Galmuri,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    analysisResult.forEachIndexed { index, item ->
                        AnalysisItemRow(item)
                        if (index < analysisResult.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 0.5.dp,
                                color = Color(0xFFEEEEEE)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisItemRow(item: AiAnalysisItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "[ ${item.item} ]",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CustomBrown, // 항목 이름도 갈색 계열
            fontFamily = Galmuri
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "👍 ${item.pros}",
            fontSize = 13.sp,
            color = Color(0xFF558B2F), // 차분한 녹색
            fontFamily = Galmuri,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "👎 ${item.cons}",
            fontSize = 13.sp,
            color = Color(0xFFC62828), // 차분한 빨강
            fontFamily = Galmuri,
            lineHeight = 18.sp
        )
    }
}