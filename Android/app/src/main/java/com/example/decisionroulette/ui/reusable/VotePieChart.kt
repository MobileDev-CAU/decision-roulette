package com.example.decisionroulette.ui.reusable


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class PieSlice(
    val color: Color,
    val ratio: Float // 0.0 ~ 1.0 사이의 비율
)
@Composable
fun VotePieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 100.dp
) {
    Canvas(modifier = modifier.size(chartSize)) {
        var currentStartAngle = -90f

        slices.forEach { slice ->
            val sweepAngle = slice.ratio * 360f

            drawArc(
                color = slice.color,
                startAngle = currentStartAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(0f, 0f),
                size = size
            )

            currentStartAngle += sweepAngle // 다음 시작 각도 업데이트
        }
    }
}
