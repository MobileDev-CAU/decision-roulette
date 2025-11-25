package com.example.decisionroulette.ui.votelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.ui.home.HomeUiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


sealed interface VoteListUiEvent {
    object NavigateToVoteStatus : VoteListUiEvent
}



class VoteListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VoteListState())
    val uiState: StateFlow<VoteListState> = _uiState.asStateFlow()

    private val _events = Channel<VoteListUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadVoteItems()
    }

    private fun loadVoteItems() {

    }

    fun onVoteItemClicked() {
        viewModelScope.launch {
            _events.send(VoteListUiEvent.NavigateToVoteStatus)
        }
    }




}