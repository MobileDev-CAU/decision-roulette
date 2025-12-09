package com.example.decisionroulette.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.data.repository.VoteRepository
import com.example.decisionroulette.api.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle

data class OptionItem(val id: Int, val title: String, val currentVotes: Double = 0.0)

data class VoteUiState(
    val options: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val voteId: Long = -1L,
    val title: String = "Loading..."
)

sealed interface VoteUiEvent {
    object NavigateToBack : VoteUiEvent
    data class NavigateToRoulette(val voteId: Long, val rouletteId: Int) : VoteUiEvent
    object NavigateToVoteClear : VoteUiEvent
}

class VoteViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val voteRepository: VoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val TAG = "VoteViewModel"

    private val currentVoteId: Long = savedStateHandle.get<String>("voteId")?.toLongOrNull() ?: -1L

    private val _uiState = MutableStateFlow(VoteUiState(voteId = currentVoteId))
    val uiState: StateFlow<VoteUiState> = _uiState.asStateFlow()

    private val _events = Channel<VoteUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (currentVoteId > 0) {
            loadVoteDetails(currentVoteId)
        } else {
            _uiState.update { it.copy(errorMessage = "Navigation Error: No valid voting ID was found.") }
        }
    }

    fun loadVoteDetails(voteId: Long) {
        if (voteId <= 0) {
            _uiState.update { it.copy(errorMessage = "Invalid Vote ID.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Repository 호출 (GET /vote/{voteId} 호출)
            val result = voteRepository.getVoteDetail(voteId)

            result.onSuccess { voteDetail ->
                val options = voteDetail.items.mapIndexed { index, voteItem ->
                    OptionItem(
                        id = index,
                        title = voteItem.name,
                        currentVotes = voteItem.voteRate // 투표율(%) 반영
                    )
                }

                Log.d(TAG, "Successful loading of voting details: ${options.size} items")

                _uiState.update {
                    it.copy(
                        options = options,
                        isLoading = false,
                        title = voteDetail.title
                    )
                }
            }.onFailure { throwable ->
                val errorMessage = throwable.message ?: "Failed to retrieve voting details due to unknown error."
                Log.e(TAG, "Failed to load voting details", throwable)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    fun vote(selectedOptionId: Int?) {
        if (selectedOptionId == null) return

        viewModelScope.launch {
            // 선택된 항목 및 투표 ID 유효성 검사
            val selectedItem = _uiState.value.options.find { it.id == selectedOptionId }
            val currentVoteId = _uiState.value.voteId

            if (selectedItem == null || currentVoteId <= 0) {
                _uiState.update { it.copy(errorMessage = "Voting information is invalid.") }
                return@launch
            }

            // AuthRepository에서 현재 사용자 ID를 가져옴
            val currentUserId = authRepository.getCurrentUserId()

            if (currentUserId == null || currentUserId <= 0) {
                _uiState.update { it.copy(errorMessage = "Login is required or a valid user ID was not found.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 투표 API 호출 로직
            val result = voteRepository.selectVote(
                voteId = currentVoteId, // Long 타입 그대로 사용
                selectedOptionName = selectedItem.title, // 항목의 이름으로 투표한다고 가정
                userId = currentUserId
            )

            result.onSuccess {
                Log.d(TAG, "Vote Transfer Successful: ${selectedItem.title}")

                // UI 상태 업데이트 및 이벤트 전송

                loadVoteDetails(currentVoteId)

                _events.send(VoteUiEvent.NavigateToVoteClear)

            }.onFailure { throwable ->
                // 실패 시: 에러 메시지 업데이트 및 로딩 종료
                val errorMessage = throwable.message ?: "Failed to send the vote."
                Log.e(TAG, "Failed to send a vote", throwable)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    // Back 버튼 클릭 이벤트 함수 추가
    fun onBackButtonClicked() {
        viewModelScope.launch {
            _events.send(VoteUiEvent.NavigateToBack)
        }
    }

    // 룰렛 시작 버튼 클릭 함수 (MyVoteScreen에서 사용)
    fun onRouletteStartClicked() {
        if (currentVoteId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = voteRepository.getVoteRouletteDetail(currentVoteId)

            result.onSuccess { response ->
                _uiState.update { it.copy(isLoading = false) }

                // 성공하면 진짜 rouletteId를 담아서 이벤트 전송
                _events.send(VoteUiEvent.NavigateToRoulette(
                    voteId = currentVoteId,
                    rouletteId = response.rouletteId
                ))
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to retrieve roulette information: ${e.message}") }
            }
        }
    }

    companion object {
        fun provideFactory(
            voteRepository: VoteRepository,
            authRepository: AuthRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: androidx.lifecycle.viewmodel.CreationExtras
            ): T {
                val savedStateHandle = extras.createSavedStateHandle()

                return VoteViewModel(
                    savedStateHandle = savedStateHandle,
                    voteRepository = voteRepository,
                    authRepository = authRepository
                ) as T
            }
        }
    }
}
