package com.example.decisionroulette.ui.votelist

import android.util.Log
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
    data class NavigateToVoteStatus(
        val voteId: Long,
        val isMyVote: Boolean
    ) : VoteListUiEvent
}

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

    fun loadVoteItems() {
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
                // 실패 시: 에러 메시지 업데이트 및 로딩 종료
                val errorMessage = throwable.message ?: "투표 목록 로드 실패"

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
//            loadVoteItems()
            val currentList = _uiState.value.voteItems
            val clickedItem = currentList.find { it.voteId == voteId }

            if (clickedItem != null) {
                val currentUserNickname = authRepository.getCurrentUserNickname()

                Log.d("VoteListVM", "클릭한 투표 ID: $voteId, 작성자: ${clickedItem.userNickname}, 나: $currentUserNickname")

                val isMyVote = !currentUserNickname.isNullOrBlank() && currentUserNickname == clickedItem.userNickname

                Log.d("VoteListVM", "이동 결정: isMyVote=$isMyVote (투표 ID: $voteId)")

                _events.send(VoteListUiEvent.NavigateToVoteStatus(
                    voteId = voteId,
                    isMyVote = clickedItem.isMyVote // 👈 계산된 isMyVote 값 사용
                ))
            } else {
                val loadedIds = currentList.map { it.voteId }
                Log.e("VoteListVM", "에러: 클릭한 아이템($voteId)을 찾을 수 없음. 현재 로드된 ID 목록: $loadedIds")
                loadVoteItems()
            }
        }
    }
}