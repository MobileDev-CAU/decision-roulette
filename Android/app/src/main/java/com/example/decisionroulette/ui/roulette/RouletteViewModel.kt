package com.example.decisionroulette.ui.roulette

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
        val randomResult = _uiState.value.items.random()

        _uiState.update {
            it.copy(
                isSpinning = false,
                spinResult = randomResult,
                showResultDialog = true
            )
        }
    }

    fun closeDialog() {
        _uiState.update { it.copy(showResultDialog = false) }
    }

    // [버튼 1] 선택 확정하기
    fun saveFinalChoice(finalChoice: String) {
        closeDialog()

        // 1. 룰렛 결과: _uiState.value.spinResult
        // 2. 최종 선택: finalChoice
        // 3. 비교: 만약 둘이 다르면 -> "사용자가 룰렛을 거부함" 데이터 기록

        println("룰렛 결과: ${_uiState.value.spinResult}, 사용자의 선택: $finalChoice")

        // TODO: 여기서 백엔드 API 호출 (finalChoice 전송)
    }
//    fun confirmSelection() {
//        closeDialog()
//        // TODO: 서버에 확정 API 보내기
//    }

    // [버튼 2] 룰렛 다시 돌리기 (팝업 닫고 바로 다시 스핀)
    fun retrySpin() {
        closeDialog()
    }

    // [버튼 3] 유저 투표 올리기
    fun uploadVote() {
        closeDialog()
        // TODO: 투표 화면으로 이동
    }

    fun addDummyItem() {
        // 현재 아이템 리스트 가져오기
        val currentItems = _uiState.value.items.toMutableList()

        // 새 아이템 추가 (예: 메뉴 6, 메뉴 7...)
        currentItems.add("메뉴 ${currentItems.size + 1}")

        // 상태 업데이트 -> 화면이 자동으로 다시 그려짐!
        _uiState.value = _uiState.value.copy(items = currentItems)
    }
}