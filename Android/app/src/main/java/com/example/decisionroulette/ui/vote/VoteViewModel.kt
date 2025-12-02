package com.example.decisionroulette.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// VoteScreen에서 사용하는 OptionItem과 동일하게 정의하거나, import 합니다.
data class OptionItem(val id: Int, val title: String, val currentVotes: Int = 0)

sealed interface VoteUiEvent {
    object NavigateToBack : VoteUiEvent         // 이전 화면으로 이동
    object NavigateToRoulette : VoteUiEvent     // 룰렛 화면으로 이동 (투표 완료 후)

    object NavigateToVoteClear : VoteUiEvent     // 룰렛 화면으로 이동 (투표 완료 후)

}

class VoteViewModel : ViewModel() {

    // 1. 이벤트 스트림 (내비게이션 처리용)
    private val _events = Channel<VoteUiEvent>()
    val events = _events.receiveAsFlow()

    // 2. 현재 투표 항목 목록 (더미 데이터 포함)
    private val _options = MutableStateFlow(
        listOf(
            OptionItem(1, "치킨", 10),
            OptionItem(2, "회", 5),
            OptionItem(3, "피자", 8),
            OptionItem(4, "도넛", 2)
        )
    )
    val options: StateFlow<List<OptionItem>> = _options.asStateFlow()

    // 3. 투표 처리 함수 (투표 성공 후 내비게이션 이벤트 전송)
    fun vote(selectedOptionId: Int?) {
        if (selectedOptionId == null) return

        viewModelScope.launch {
            val updatedList = _options.value.map { item ->
                if (item.id == selectedOptionId) {
                    // 선택된 항목의 투표 수를 1 증가시킵니다. (더미 로직)
                    item.copy(currentVotes = item.currentVotes + 1)
                } else {
                    item
                }
            }
            _options.value = updatedList

            // TODO: 실제로는 여기에 API 호출 및 서버 업데이트 로직을 넣어야 합니다.

            // 투표가 성공했다고 가정하고, 룰렛 화면으로 이동 이벤트를 발생시킵니다.
            _events.send(VoteUiEvent.NavigateToRoulette)
        }
    }

    // 4. Back 버튼 클릭 이벤트 함수 추가
    fun onBackButtonClicked() {
        viewModelScope.launch {
            _events.send(VoteUiEvent.NavigateToBack)
        }
    }
}