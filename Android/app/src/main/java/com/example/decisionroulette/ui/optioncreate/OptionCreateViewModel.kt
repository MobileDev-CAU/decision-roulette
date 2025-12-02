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
    fun addOption() {
        val newId = nextId.getAndIncrement()
        val newOption = Option(id = newId, value = "")
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

    fun onSaveButtonClicked() {
        val validOptions = _options.filter { it.value.isNotBlank() }
        val itemsList = validOptions.map { it.value }
        val title = uiState.topicTitle

        if (itemsList.isEmpty()) return // 빈 값이면 요청 안 함

        viewModelScope.launch {
            // 로딩 시작
            uiState = uiState.copy(isLoading = true)

            // API 호출 (ownerId는 10으로 가정)
            val result = repository.createRoulette(title, itemsList, ownerId = 10)

            result.onSuccess { response ->
                println("룰렛 생성 성공! ID: ${response.rouletteId}")
                _events.send(OptionCreateUiEvent.NavigateToRoulette(response.rouletteId))
            }.onFailure { e ->
                println("룰렛 생성 실패: ${e.message}")
                // 실패 처리 (토스트 메시지 등)
            }

            // 로딩 종료
            uiState = uiState.copy(isLoading = false)
        }
    }

   // 백 버튼
    fun onBackButtonClicked() {
        viewModelScope.launch {
            _events.send(OptionCreateUiEvent.NavigateToBack)
        }
    }

    // ai 추천
    fun onAiButtonClicked() {
        viewModelScope.launch {
            _events.send(OptionCreateUiEvent.NavigateAi)
        }
    }
}