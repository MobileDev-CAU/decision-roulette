package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

// 🚨 LocalContext, LocalDensity 오류를 해결하고 Scrollbar Thumb의 위치와 크기를 계산하여 그리는 컴포넌트입니다.
@Composable
fun VerticalScrollbarThumb(
    listScrollState: ScrollState,
    modifier: Modifier = Modifier,
    thickness: Dp = 4.dp, // 스크롤 바 두께
    color: Color = Color.Gray.copy(alpha = 0.5f) // 스크롤 바 색상
) {
    // 1. Composable Context 값을 읽어 변수에 저장 (오류 방지)
    val density = LocalDensity.current
    val context = LocalContext.current

    // 2. 부모 Box의 높이 (px)를 저장하여 썸의 위치 계산에 사용
    var parentHeightPx by remember { mutableStateOf(0f) }

    // 스크롤 가능한 콘텐츠의 전체 높이
    val maxScroll = listScrollState.maxValue
    val currentScroll = listScrollState.value

    // 스크롤이 불가능하면 썸을 표시하지 않습니다.
    if (maxScroll == 0) return

    // 뷰포트 높이 (px)
    val viewportHeightPx = with(density) { listScrollState.viewportSize.toDp().toPx() }
    // 전체 콘텐츠 높이 (스크롤 가능 범위 + 뷰포트)
    val contentHeightPx = maxScroll + viewportHeightPx

    // 썸(Thumb)의 높이 비율 (뷰포트 / 전체 콘텐츠)
    val thumbHeightFraction = if (contentHeightPx > 0) viewportHeightPx / contentHeightPx else 1f

    // 썸의 최소 높이를 dp로 설정 (너무 작아지지 않도록)
    val minThumbHeightDp = 24.dp
    val minThumbHeightPx = with(density) { minThumbHeightDp.toPx() }

    // 썸의 실제 높이 (px), 최소 높이와 계산된 높이 중 더 큰 값
    val actualThumbHeightPx = max(minThumbHeightPx, viewportHeightPx * thumbHeightFraction)

    // 썸의 Y축 오프셋 비율 (현재 스크롤 위치 / 전체 스크롤 가능 범위)
    val thumbOffsetFraction = if (maxScroll > 0) currentScroll.toFloat() / maxScroll.toFloat() else 0f

    // 썸이 이동할 수 있는 전체 거리 (부모 Box 높이 - 썸 높이)
    val travelDistancePx = parentHeightPx - actualThumbHeightPx

    // 썸의 Y축 최종 이동 위치 (px)
    val thumbOffsetYPx = thumbOffsetFraction * travelDistancePx

    Box(
        modifier = modifier
            .width(thickness)
            .fillMaxHeight()
            // 부모 Box의 높이를 측정
            .onGloballyPositioned { coordinates ->
                parentHeightPx = coordinates.size.height.toFloat()
            }
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Transparent) // 썸을 감싸는 컨테이너
    ) {
        if (parentHeightPx > 0f) {
            Spacer(
                modifier = Modifier
                    .width(thickness)
                    // 썸의 높이를 비율에 따라 설정
                    .fillMaxHeight(fraction = thumbHeightFraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .align(Alignment.TopCenter)
                    // 썸의 Y축 위치를 조정 (스크롤 위치에 따라 이동)
                    .graphicsLayer {
                        translationY = thumbOffsetYPx
                    }
            )
        }
    }
}