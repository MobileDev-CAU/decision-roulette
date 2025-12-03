package com.example.decisionroulette.ui.votelist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.api.auth.AuthRepository
import com.example.decisionroulette.data.repository.VoteRepository
import com.example.decisionroulette.api.vote.VoteListItem // API 모델 임포트
// import com.example.decisionroulette.ui.votelist.VoteListState // 같은 패키지이므로 import는 생략되거나 자동으로 처리됨
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
            // 1. 로딩 상태 시작
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 2. Repository 호출 (결과는 Kotlin 표준 Result<T> 형태)
            val result = repository.getVoteList()

            // 3. Kotlin 표준 Result<T> 처리
            result.onSuccess { voteList ->
                // 성공 시: 데이터 업데이트 및 로딩 종료
                _uiState.update {
                    it.copy(
                        voteItems = voteList,
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
                    isMyVote = isMyVote
                ))
            } else {
                val loadedIds = currentList.map { it.voteId }
                Log.e("VoteListVM", "에러: 클릭한 아이템($voteId)을 찾을 수 없음. 현재 로드된 ID 목록: $loadedIds")
                loadVoteItems()
            }
        }
    }
}