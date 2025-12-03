package com.example.decisionroulette.ui.votelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.api.auth.AuthRepository
import com.example.decisionroulette.data.repository.VoteRepository
import com.example.decisionroulette.api.vote.VoteListItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface VoteListUiEvent {
    // 네비게이션 이벤트에 화면 타입(MY/OTHER) 정보가 포함됩니다.
    data class NavigateToVoteStatus(
        val voteId: Long,
        val isMyVote: Boolean
    ) : VoteListUiEvent
}

// ----------------------------------------------------------
// 🚨 VoteListViewModel
// ----------------------------------------------------------
class VoteListViewModel(
    private val repository: VoteRepository = VoteRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoteListState())
    val uiState: StateFlow<VoteListState> = _uiState.asStateFlow()

    private val _events = Channel<VoteListUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadVoteItems()
    }

    /**
     * 투표 목록을 비동기로 불러와 isMyVote를 계산하여 UI State에 저장하는 함수
     */
    private fun loadVoteItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. 현재 로그인된 사용자 닉네임 가져오기
            val currentUserNickname = authRepository.getCurrentUserNickname()

            repository.getVoteList().onSuccess { voteList ->
                // 2. API 모델(VoteListItem)을 UI 모델(VoteItemUiModel)로 변환하며 isMyVote 플래그 계산
                val uiModels = voteList.map { apiItem ->
                    val isMyVote = currentUserNickname != null && currentUserNickname == apiItem.userNickname

                    VoteItemUiModel(
                        voteId = apiItem.voteId,
                        userNickname = apiItem.userNickname,
                        title = apiItem.title,
                        itemCount = apiItem.itemCount,
                        isMyVote = isMyVote       // 👈 계산된 값 삽입
                    )
                }

                // 3. UI 모델 리스트로 상태 업데이트
                _uiState.update {
                    it.copy(
                        voteItems = uiModels,
                        isLoading = false
                    )
                }
            }.onFailure { throwable ->
                val errorMessage = throwable.message ?: "Failed to load the voting list with unknown error."
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }


    fun onVoteItemClicked(voteId: Long) {
        viewModelScope.launch {
            val clickedItem = _uiState.value.voteItems.find { it.voteId == voteId }

            if (clickedItem != null) {
                // 🚨 State에 이미 isMyVote 정보가 있으므로 바로 사용 🚨
                _events.send(VoteListUiEvent.NavigateToVoteStatus(
                    voteId = voteId,
                    isMyVote = clickedItem.isMyVote // 👈 계산된 isMyVote 값 사용
                ))
            }
        }
    }
}