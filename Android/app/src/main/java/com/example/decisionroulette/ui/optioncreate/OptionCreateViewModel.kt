package com.example.decisionroulette.ui.optioncreate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import com.example.decisionroulette.api.roulette.RouletteRepository
import com.example.decisionroulette.ui.auth.TokenManager


// data/Option.kt


sealed interface OptionCreateUiEvent {
    data class NavigateToRoulette(val rouletteId: Int) : OptionCreateUiEvent
    object NavigateAi: OptionCreateUiEvent
    object NavigateToBack : OptionCreateUiEvent
}

class OptionCreateViewModel : ViewModel() {
    private val repository = RouletteRepository()
    private val _events = Channel<OptionCreateUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()


    private val nextId = AtomicInteger(1)

    private val _options = mutableStateListOf<Option>()

    var uiState by mutableStateOf(OptionCreateUiState(options = _options))
        private set

    init {
        repeat(2) {
            addOption()
        }
    }

    fun updateTitle(title: String) {
        uiState = uiState.copy(topicTitle = title)
    }

    // 옵션 추가
    fun addOption(initialValue: String = "") {
        val newId = nextId.getAndIncrement()
        val newOption = Option(id = newId, value = initialValue)
        _options.add(newOption)
    }

    // 옵션 필드 업데이트
    fun updateOptionValue(id: Int, newValue: String) {
        val index = _options.indexOfFirst { it.id == id }
        if (index != -1) {
            _options[index] = _options[index].copy(value = newValue)
        }
    }

    // 옵션 제거
    fun removeOption(id: Int) {
        _options.removeIf { it.id == id }
    }

    // AI 추천 버튼 클릭 (API 호출 -> 다이얼로그 오픈)
    fun onAiButtonClicked() {
        val title = uiState.topicTitle
        val userId = TokenManager.getUserId()

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val result = repository.getAiRecommendation(title, userId = userId)

            result.onSuccess { response ->
                // 성공 시 다이얼로그 열기 & 추천 목록 업데이트
                uiState = uiState.copy(
                    isLoading = false,
                    showAiDialog = true,
                    aiRecommendations = response.recommendations
                )
            }.onFailure {
                // 실패 시 에러 처리
                println("AI recommendation failed: ${it.message}")
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    // 다이얼로그 닫기
    fun dismissAiDialog() {
        uiState = uiState.copy(showAiDialog = false)
    }

    // 다이얼로그 완료 (선택된 항목들 추가)
    fun addAiSelectedOptions(selectedItems: List<String>) {
        selectedItems.forEach { item ->
            // 현재 옵션 목록 중 비어있는(공백) 칸 찾기
            val emptyIndex = _options.indexOfFirst { it.value.isBlank() }

            if (emptyIndex != -1) {
                // 비어있는 칸이 있으면 그 자리에 값 채워넣기
                _options[emptyIndex] = _options[emptyIndex].copy(value = item)
            } else {
                // 비어있는 칸이 없으면 새로 추가하기
                addOption(initialValue = item)
            }
        }

        dismissAiDialog()
    }

    fun onSaveButtonClicked() {
        val validOptions = _options.filter { it.value.isNotBlank() }
        val itemsList = validOptions.map { it.value }
        val title = uiState.topicTitle
        val userId = TokenManager.getUserId()

        if (itemsList.isEmpty()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            val result = repository.createRoulette(title, itemsList, ownerId = userId)

            result.onSuccess { response ->
                println("Successfully created roulette! ID: ${response.rouletteId}")
                _events.send(OptionCreateUiEvent.NavigateToRoulette(response.rouletteId))
            }.onFailure { e ->
                println("Failed to create roulette: ${e.message}")
            }

            uiState = uiState.copy(isLoading = false)
        }
    }

    fun onBackButtonClicked() {
        viewModelScope.launch {
            _events.send(OptionCreateUiEvent.NavigateToBack)
        }
    }

}