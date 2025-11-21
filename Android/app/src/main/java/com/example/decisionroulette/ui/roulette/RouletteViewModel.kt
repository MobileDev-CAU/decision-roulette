package com.example.decisionroulette.ui.roulette

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RouletteViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(RouletteUiState())
    val uiState: StateFlow<RouletteUiState> = _uiState.asStateFlow()

    init {
        // 목데이터
        _uiState.value = RouletteUiState(
            title = "저녁 메뉴",
            items = listOf("치킨", "피자", "족발", "마라탕", "샐러드"),
            top3Keywords = listOf("1. 치킨", "2. 마라탕", "3. 떡볶이")
        )
    }

    fun startSpin() {
        // 상태를 업데이트해서 스핀 시작
        _uiState.value = _uiState.value.copy(isSpinning = true)
    }

    fun onSpinFinished() {
        // 애니메이션 끝나면 상태 복구
        _uiState.value = _uiState.value.copy(isSpinning = false)
        // TODO:- 최종 결과 팝업 같은거 띄우기
    }
}