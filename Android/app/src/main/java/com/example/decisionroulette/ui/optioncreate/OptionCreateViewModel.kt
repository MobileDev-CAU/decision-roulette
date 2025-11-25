package com.example.decisionroulette.ui.optioncreate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // ⬅️ 추가
import kotlinx.coroutines.channels.Channel // ⬅️ 추가
import kotlinx.coroutines.flow.receiveAsFlow // ⬅️ 추가
import kotlinx.coroutines.launch // ⬅️ 추가
import java.util.concurrent.atomic.AtomicInteger


// data/Option.kt


sealed interface OptionCreateUiEvent {
    object NavigateToRoulette: OptionCreateUiEvent
    object NavigateAi: OptionCreateUiEvent
    object NavigateToBack : OptionCreateUiEvent
}

class OptionCreateViewModel : ViewModel() {

    private val _events = Channel<OptionCreateUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()


    private val nextId = AtomicInteger(1)

    private val _options = mutableStateListOf<Option>()

    var uiState by mutableStateOf(OptionCreateUiState(options = _options))
        private set

    init {
        repeat(3) {
            addOption()
        }
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



    //  최종 '저장' 버튼 클릭 시 모든 옵션 값을 반환하고 룰렛 화면으로 이동 이벤트를 발행

    fun onSaveButtonClicked() {
        val validOptions = _options.filter { it.value.isNotBlank() }
        println("Saving options: $validOptions")

        // TODO: 서버 저장 로직 추가

        viewModelScope.launch {
            _events.send(OptionCreateUiEvent.NavigateToRoulette)
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