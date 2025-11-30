package com.example.decisionroulette.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface HomeUiEvent {
    object NavigateToTopicCreate : HomeUiEvent
}

class HomeViewModel : ViewModel() {

    private val _events = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()


    // spin 버튼 누르면 다음 페이지로 가는 함수
    fun onRouletteButtonClicked() {

        viewModelScope.launch {
            _events.send(HomeUiEvent.NavigateToTopicCreate)
        }
    }
}