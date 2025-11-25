package com.example.decisionroulette.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface VoteUiEvent {
    object NavigateToTopicList : HomeUiEvent
}

class VoteViewModel : ViewModel() {

}