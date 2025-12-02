package com.example.decisionroulette.ui.roulette
import com.example.decisionroulette.data.RouletteItem
import com.example.decisionroulette.api.roulette.AiAnalysisItem

data class RouletteUiState(
    val isLoading: Boolean = false,            // 로딩 중인가?
    val rouletteId: Int = 0,                   // 룰렛 ID
    val title: String = "",                    // 룰렛 제목 ("저녁 메뉴")
    val items: List<RouletteItem> = emptyList(),     // 룰렛 돌릴 항목들 (["치킨", "피자"...])
    val top3Keywords: List<String> = emptyList(), // 이전 선택 TOP3
    val isVoteMode: Boolean = false,
    val targetRotation: Float = 0f,
    val spinResult: String? = null,            // 룰렛 결과 (null이면 아직 안 돌린 거)
    val isSpinning: Boolean = false,            // 현재 룰렛이 빙글빙글 도는 중인가?
    val showResultDialog: Boolean = false,
    val analysisResult: List<AiAnalysisItem> = emptyList()
)
