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
import com.example.decisionroulette.ui.auth.TokenManager
import kotlinx.coroutines.launch
import com.example.decisionroulette.data.repository.VoteRepository // ⭐ VoteRepository import

class RouletteViewModel(
    private val repository: RouletteRepository = RouletteRepository(),
    private val voteRepository: VoteRepository = VoteRepository() // ⭐ VoteRepository 주입
) : ViewModel() {
    private val _uiState = MutableStateFlow(RouletteUiState())
    val uiState: StateFlow<RouletteUiState> = _uiState.asStateFlow()

    private var originalItems: List<RouletteItem> = emptyList()

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
        val newItems = if (isVote) {
            // 투표 모드: 원본 가중치 데이터로 복구
            originalItems
        } else {
            // 기본 모드: 모든 가중치를 1.0으로 통일
            originalItems.map { it.copy(weight = 1.0f) }
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
                    // weight가 0이거나 없으면 기본 1.0 처리, 있으면 그대로 사용
                    val weight = if (dto.weight > 0) dto.weight.toFloat() else 1.0f
                    RouletteItem(name = dto.name, weight = weight)
                }
//                val uiItems = response.items.map { dto ->
//                    RouletteItem(
//                        name = dto.name,
//                        weight = dto.weight.toFloat()
//                    )
//                }
                originalItems = uiItems
                val basicItems = uiItems.map { it.copy(weight = 1.0f) }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rouletteId = response.rouletteId,
                        title = response.title,
                        items = basicItems,
                        isVoteMode = false,
                        top3Keywords = emptyList()
                    )
                }
                analyzeRoulette(response.title, uiItems.map { it.name })
            }.onFailure { e ->
                println("룰렛 상세 조회 실패: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 투표 결과로 룰렛 데이터 로드 (Vote % 모드로 시작)
    fun loadRouletteFromVote(voteId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // VoteRepository를 통해 투표 상세 정보(룰렛용) 가져오기
            val result = voteRepository.getVoteRouletteDetail(voteId)

            result.onSuccess { response ->
                // 투표 결과(voteRate)를 weight로 매핑
                val uiItems = response.items.map { dto ->

                    val minWeight = 5.0f
                    val adjustedWeight = if (dto.weight.toFloat() <= 0f) minWeight else dto.weight.toFloat()

                    RouletteItem(
                        name = dto.name,
                        weight = adjustedWeight
                    )
                }

                // 🔥 원본 데이터 저장
                originalItems = uiItems

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rouletteId = response.rouletteId,
                        title = response.title,
                        items = uiItems,
                        isVoteMode = true,
                        top3Keywords = emptyList()
                    )
                }
            }.onFailure { e ->
                println("투표 룰렛 정보 로드 실패: ${e.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // AI 분석 리포트 요청
    private fun analyzeRoulette(title: String, items: List<String>) {
        println("AI 분석 요청 시작: 제목=$title, 항목수=${items.size}")

        if (items.isEmpty()) {
            println("AI 분석 취소: 항목이 없습니다.")
            return
        }

//        val title = _uiState.value.title
//        val items = _uiState.value.items.map { it.name }

        viewModelScope.launch {
            val result = repository.analyzeRoulette(title, items)

            result.onSuccess { response ->
                println("AI 분석 성공! 결과 개수: ${response.analysis.size}")
                _uiState.update {
                    it.copy(analysisResult = response.analysis)
                }
            }.onFailure { e ->
                println("AI 분석 실패: ${e.message}")
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

    // 피드백(만족/불만족) 전송
    private fun sendFeedback(satisfied: Boolean) {
        val currentId = _uiState.value.rouletteId
        val spinResult = _uiState.value.spinResult ?: return
        val userId = TokenManager.getUserId()

        viewModelScope.launch {
            // userId 10 하드코딩 (나중에 로그인 정보로 교체)
            repository.saveFeedback(currentId, spinResult, satisfied, userId = userId)
                .onSuccess { println("피드백 전송 성공: satisfied=$satisfied") }
                .onFailure { println("피드백 전송 실패: ${it.message}") }
        }
    }

    // [버튼 1] 선택 확정하기
    fun saveFinalChoice(finalChoice: String, satisfied: Boolean) {
        closeDialog()
        val currentId = _uiState.value.rouletteId
        val spinResult = _uiState.value.spinResult ?: return
        val userId = TokenManager.getUserId()

        println("최종 선택 저장: ID=$currentId, 결과=$spinResult, 선택=$finalChoice")

        // 피드백 전송 (만족 or 불만족)
        sendFeedback(satisfied)

        // 최종 선택 저장 API 호출
        viewModelScope.launch {
            repository.saveFinalChoice(currentId, spinResult, finalChoice, userId = userId)
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
        sendFeedback(false)
    }

    // ⭐ [버튼 3] 유저 투표 올리기 로직 완성
    fun uploadVote() {
        closeDialog()
        sendFeedback(false) // 룰렛 결과를 따르지 않았으므로 불만족 피드백 전송 (가정)

        val rouletteId = _uiState.value.rouletteId
        val userId = TokenManager.getUserId()

        if (rouletteId <= 0) {
            println("투표 업로드 실패: 유효하지 않은 룰렛 ID입니다.")
            return
        }

        viewModelScope.launch {
            // VoteRepository를 사용하여 룰렛을 투표로 업로드
            val result = voteRepository.uploadVote(rouletteId = rouletteId, userId = userId)

            result.onSuccess { response ->
                println("투표 업로드 성공: Vote ID: ${response.voteId}, Message: ${response.message}")
                // TODO: 투표 화면으로 이동 이벤트 발생 (예: NavigateToVoteList)
            }.onFailure { e ->
                println("투표 업로드 실패: ${e.message}")
                // 사용자에게 실패 메시지를 보여줄 수 있는 UI 상태 업데이트 필요
            }
        }
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