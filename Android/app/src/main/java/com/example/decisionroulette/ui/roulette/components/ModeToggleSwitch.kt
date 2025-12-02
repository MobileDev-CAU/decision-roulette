package com.example.decisionroulette.ui.roulette.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.ui.theme.Galmuri

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
        ToggleButton(
            text = "Basic",
            isSelected = !isVoteMode,
            onClick = { onToggle(false) }
        )

        ToggleButton(
            text = "Vote %",
            isSelected = isVoteMode,
            onClick = { onToggle(true) }
        )
    }
}

@Composable
private fun ToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
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