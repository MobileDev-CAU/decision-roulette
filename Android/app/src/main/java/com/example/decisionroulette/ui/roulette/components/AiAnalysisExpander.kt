package com.example.decisionroulette.ui.roulette.components

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

@Composable
fun AiAnalysisExpander(items: List<RouletteItem>) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "✨ AI Analysis Report", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = Galmuri)
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = Color.Gray
            )
        }

        // 내용
        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Text(
                        text = "[ ${item.name} ]",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = Galmuri
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "👍 Pros: Good for health and tasty.",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50),
                        fontFamily = Galmuri
                    )
                    Text(
                        text = "👎 Cons: Can be expensive.",
                        fontSize = 12.sp,
                        color = Color(0xFFE57373),
                        fontFamily = Galmuri
                    )
                }
            }
        }
    }
}