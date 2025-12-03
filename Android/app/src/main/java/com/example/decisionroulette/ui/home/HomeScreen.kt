package com.example.decisionroulette.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.R
import com.example.decisionroulette.ui.theme.Galmuri

private val CustomBrown = Color(0xFF685C57)

@Composable
fun HomeScreen(
    onNavigateToTopicCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 타이틀
        Text(
            text = "Today's Roulette",
            modifier = Modifier.padding(top = 120.dp),
            fontSize = 32.sp,
            fontFamily = Galmuri,
            fontWeight = FontWeight.Bold,
            color = CustomBrown
        )

        // 룰렛 이미지
        Image(
            painter = painterResource(id = R.drawable.roulette),
            contentDescription = "Roulette Image",
            modifier = Modifier
                .padding(vertical = 50.dp)
                .size(300.dp)
        )

        // START 버튼 (갈색 테마 적용)
        Button(
            onClick = onNavigateToTopicCreate,
            modifier = Modifier
                .width(250.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomBrown
            ),
            shape = RoundedCornerShape(12.dp) // 둥근 모서리
        ) {
            Text(
                text = "START",
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = Galmuri,
                fontWeight = FontWeight.Bold
            )
        }
    }
}