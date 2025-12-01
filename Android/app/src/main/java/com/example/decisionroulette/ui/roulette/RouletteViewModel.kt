package com.example.decisionroulette.ui.roulette

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.data.RouletteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.isNotEmpty
import kotlin.collections.toMutableList
import com.example.decisionroulette.api.roulette.RouletteRepository
import kotlinx.coroutines.launch

class RouletteViewModel: ViewModel() {
    private val repository = RouletteRepository()
    private val _uiState = MutableStateFlow(RouletteUiState())
    val uiState: StateFlow<RouletteUiState> = _uiState.asStateFlow()
//    private val rawVoteItems = listOf(
//        RouletteItem("파스타", 0.2f), // 20%
//        RouletteItem("국밥", 0.2f),   // 20%
//        RouletteItem("돈까스", 0.2f),  // 20%
//        RouletteItem("김밥", 0.2f),   // 20%
//        RouletteItem("초밥", 0.2f),   // 20%
//    )

//    private val rawVoteItems = listOf(
//        RouletteItem("파스타", 0.1f),
//        RouletteItem("1", 0.1f),
//        RouletteItem("2", 0.1f),
//        RouletteItem("3", 0.1f),
//        RouletteItem("4", 0.1f),
//        RouletteItem("5", 0.1f),
//        RouletteItem("김밥", 0.2f),
//        RouletteItem("초밥", 0.2f),
//    )

    init {
        _uiState.update {
            it.copy(
                title = "점심 메뉴",
                top3Keywords = listOf("1. 파스타", "2. 국밥", "3. 돈까스")
            )
        }
        toggleMode(false)
    }

    fun toggleMode(isVote: Boolean) {
        val currentItems = _uiState.value.items

        val newItems = if (isVote) {
            // 투표 모드: 가중치 유지 (서버에서 받은 값)
            currentItems
        } else {
            // 기본 모드: 가중치 1.0으로 통일
            currentItems.map { it.copy(weight = 1.0f) }
        }

        _uiState.update {
            it.copy(
                isVoteMode = isVote,
                items = newItems
            )
        }
    }

    fun startSpin(currentRotation: Float) {
        if (_uiState.value.isSpinning) return

        val currentItems = _uiState.value.items
        if (currentItems.isEmpty()) return

        // 1. 가중치대로 당첨 아이템 뽑기
        val wonItem = getWeightedRandomItem(currentItems)

        // 2. 당첨 아이템이 화살표(12시)에 오기 위한 '추가 회전 각도' 정밀 계산
        val targetAngle = calculateTargetAngle(currentRotation, currentItems, wonItem)

        _uiState.update {
            it.copy(
                isSpinning = true,
                showResultDialog = false,
                spinResult = wonItem.name,
                targetRotation = targetAngle // 계산된 추가 각도 저장
            )
        }
    }

    private fun calculateTargetAngle(currentRotation: Float, items: List<RouletteItem>, wonItem: RouletteItem): Float {
        val totalWeight = items.sumOf { it.weight.toDouble() }.toFloat()

        // (A) 당첨 아이템이 룰렛에서 차지하는 범위(Start ~ End 각도) 구하기
        var startAngle = 0f
        var endAngle = 0f
        var accumulatedWeight = 0f

        for (item in items) {
            val sweepAngle = (item.weight / totalWeight) * 360f
            if (item == wonItem) {
                startAngle = (accumulatedWeight / totalWeight) * 360f
                endAngle = startAngle + sweepAngle
                break
            }
            accumulatedWeight += item.weight
        }

        // (B) 해당 범위 내에서 멈출 '랜덤 위치' 결정 (경계선 걸림 방지 여유 10%)
        val randomOffset = (Math.random() * 0.8 + 0.1).toFloat() // 0.1 ~ 0.9
        val targetCenterAngle = startAngle + (endAngle - startAngle) * randomOffset

        // (C) 현재 룰렛 상태를 고려한 회전량 계산
        // 공식: (360 - 목표위치) - (현재위치 % 360) + 최소회전(5바퀴)
        // 나머지가 음수일 수 있으므로 (currentRotation % 360 + 360) % 360 처리

        val currentAnglePos = (currentRotation % 360f + 360f) % 360f
        val angleToRotate = (360f - targetCenterAngle) - currentAnglePos

        // 음수면 한 바퀴(360) 더 돌려서 양수로 만듦
        val finalRotation = if (angleToRotate < 0) angleToRotate + 360f else angleToRotate

        return finalRotation + (360f * 5) // 최소 5바퀴 추가
    }

    fun loadRouletteDetail(rouletteId: Int) {
        viewModelScope.launch {
            // 로딩 시작
            _uiState.update { it.copy(isLoading = true) }

            // API 호출
            val result = repository.getRouletteDetail(rouletteId)

            result.onSuccess { response ->
                // DTO -> UI Model 변환
                val uiItems = response.items.map { dto ->
                    RouletteItem(
                        name = dto.name,
                        weight = dto.weight.toFloat()
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rouletteId = response.rouletteId,
                        title = response.title,
                        items = uiItems,
                        top3Keywords = emptyList()
                    )
                }
            }.onFailure { e ->
                println("룰렛 상세 조회 실패: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSpinFinished() {
        _uiState.update {
            it.copy(
                isSpinning = false,
                showResultDialog = true
            )
        }
    }

    private fun getWeightedRandomItem(items: List<RouletteItem>): RouletteItem {
        val totalWeight = items.sumOf { it.weight.toDouble() }
        val randomValue = Math.random() * totalWeight
        var currentWeight = 0.0
        for (item in items) {
            currentWeight += item.weight
            if (randomValue <= currentWeight) {
                return item
            }
        }
        return items.last() // 혹시 모를 오차 대비
    }

    fun closeDialog() {
        _uiState.update { it.copy(showResultDialog = false) }
    }

    // [버튼 1] 선택 확정하기
    fun saveFinalChoice(finalChoice: String) {
        closeDialog()
        val currentId = _uiState.value.rouletteId
        val spinResult = _uiState.value.spinResult ?: return

        println("최종 선택 저장: ID=$currentId, 결과=$spinResult, 선택=$finalChoice")

        // 최종 선택 저장 API 호출
        viewModelScope.launch {
            // userId는 로그인된 정보를 가져와야 함 (여기선 10 하드코딩)
            repository.saveFinalChoice(currentId, spinResult, finalChoice, userId = 10)
                .onSuccess {
                    println("저장 성공")
                }
                .onFailure {
                    println("저장 실패: ${it.message}")
                }
        }
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
        currentItems.add(RouletteItem("메뉴 ${currentItems.size + 1}", 1.0f))
        // 상태 업데이트 -> 화면이 자동으로 다시 그려짐!
        _uiState.update { it.copy(items = currentItems) }
    }
}