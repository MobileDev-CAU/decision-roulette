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
import com.example.decisionroulette.data.RouletteItem
import com.example.decisionroulette.ui.theme.Galmuri
import com.example.decisionroulette.api.roulette.AiAnalysisItem

@Composable
fun AiAnalysisExpander(
    analysisResult: List<AiAnalysisItem>,
    isLoading: Boolean = false // 로딩 상태가 필요하면 사용
) {
    // 접힘/펼침 상태 관리
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // .heightIn(max = 500.dp) // 필요 시 높이 제한
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded } // 박스 클릭 시 토글
    ) {
        // [헤더] 제목 + 화살표
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
                color = Color.Black
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = Color.Gray
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                if (analysisResult.isEmpty()) {
                    // 데이터가 아직 없을 때
                    Text(
                        text = "Analyzing roulette items...",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = Galmuri,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    // 데이터가 있을 때 리스트 표시
                    analysisResult.forEachIndexed { index, item ->
                        AnalysisItemRow(item)
                        if (index < analysisResult.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray
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
        // 항목 이름
        Text(
            text = "[ ${item.item} ]",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = Galmuri
        )
        Spacer(modifier = Modifier.height(4.dp))

        // 장점 (Pros)
        Text(
            text = "👍 ${item.pros}",
            fontSize = 13.sp,
            color = Color(0xFF2E7D32), // 짙은 녹색
            fontFamily = Galmuri,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(2.dp))

        // 단점 (Cons)
        Text(
            text = "👎 ${item.cons}",
            fontSize = 13.sp,
            color = Color(0xFFC62828), // 짙은 빨강
            fontFamily = Galmuri,
            lineHeight = 18.sp
        )
    }
}