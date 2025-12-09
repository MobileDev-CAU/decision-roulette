package com.example.decisionroulette.ui.editoption

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.api.roulette.RouletteRepository
import com.example.decisionroulette.ui.optioncreate.Option
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface EditOptionUiEvent {
    object NavigateBack : EditOptionUiEvent
    object NavigateToRoulette : EditOptionUiEvent
}

class EditOptionViewModel : ViewModel() {

    private val repository = RouletteRepository()

    private val _events = Channel<EditOptionUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _options = mutableStateListOf<Option>()

    var uiState by mutableStateOf(EditOptionUiState(options = _options))
        private set

    //  화면 진입 시 데이터 로드
    fun loadRouletteData(rouletteId: Int) {
        if (uiState.rouletteId != 0) return // 이미 로드했으면 패스

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, rouletteId = rouletteId)

            repository.getRouletteDetail(rouletteId).onSuccess { response ->
                // 서버 데이터 -> UI용 Option 변환
                _options.clear()
                response.items.forEach { itemDto ->
                    _options.add(Option(id = itemDto.itemId, value = itemDto.name))
                }

                uiState = uiState.copy(
                    topicTitle = response.title,
                    isLoading = false
                )
            }.onFailure { e ->
                uiState = uiState.copy(isLoading = false, error = e.message)
            }
        }
    }

    // 옵션 값 변경 (UI 업데이트)
    fun updateOptionValue(id: Int, newValue: String) {
        val index = _options.indexOfFirst { it.id == id }
        if (index != -1) {
            _options[index] = _options[index].copy(value = newValue)
        }
    }

    // 수정 완료 (Save)
    fun onSaveButtonClicked() {
        val rouletteId = uiState.rouletteId
        if (rouletteId == 0) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            _options.forEach { option ->
                repository.updateItemName(rouletteId, option.id, option.value)
            }

            // 모든 작업 완료 후 이동
            uiState = uiState.copy(isLoading = false)
            _events.send(EditOptionUiEvent.NavigateToRoulette)
        }
    }

    fun onBackButtonClicked() {
        viewModelScope.launch { _events.send(EditOptionUiEvent.NavigateBack) }
    }
}