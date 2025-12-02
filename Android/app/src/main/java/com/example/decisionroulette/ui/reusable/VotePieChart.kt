package com.example.decisionroulette.ui.reusable


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 파이 차트의 각 조각을 나타내는 데이터 클래스.
 */
data class PieSlice(
    val color: Color,
    val ratio: Float // 0.0 ~ 1.0 사이의 비율
)

/**
 * 재사용 가능한 투표 결과 파이 차트 컴포넌트.
 *
 * @param slices 각 투표 항목의 색상과 비율 목록.
 * @param chartSize 차트의 크기 (Dp).
 */
@Composable
fun VotePieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 100.dp
) {
    Canvas(modifier = modifier.size(chartSize)) {
        var currentStartAngle = -90f

        slices.forEach { slice ->
            val sweepAngle = slice.ratio * 360f // 각도 계산

            drawArc(
                color = slice.color,
                startAngle = currentStartAngle,
                sweepAngle = sweepAngle,
                useCenter = true, // 파이 조각 모양
                topLeft = Offset(0f, 0f),
                size = size
            )

            currentStartAngle += sweepAngle // 다음 시작 각도 업데이트
        }
    }
}

/* 이런식으로 사용하면 됨

* // 1. 필요한 데이터 정의
    val currentVoteData = listOf(
        PieSlice(Color.Red, 0.40f), // 40%
        PieSlice(Color.Blue, 0.35f), // 35%
        PieSlice(Color.Gray, 0.25f)  // 25%
    )

    Column(
        // ... 생략
    ) {
        // 2. 재사용 컴포넌트 호출
        VotePieChart(
            slices = currentVoteData,
            chartSize = 150.dp, // 필요한 크기 지정
            modifier = Modifier.padding(20.dp)


            */