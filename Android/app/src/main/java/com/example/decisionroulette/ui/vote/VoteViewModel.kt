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
import androidx.lifecycle.createSavedStateHandle // ⭐ 필수 import 추가
import com.example.decisionroulette.ui.roulettelist.TopicCreateUiEvent

// UI에서 사용할 항목 모델 정의
data class OptionItem(val id: Int, val title: String, val currentVotes: Int = 0)

// 투표 화면의 UI 상태 정의
data class VoteUiState(
    val options: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val voteId: Long = -1L,
    val title: String = "Loading..." // ⭐ 투표 제목 필드 추가 및 초기화
)

sealed interface VoteUiEvent {
    object NavigateToBack : VoteUiEvent
    object NavigateToRoulette : VoteUiEvent
    object NavigateToVoteClear : VoteUiEvent // 투표 완료 후 돌아가기 이벤트
}

class VoteViewModel(
    private val savedStateHandle: SavedStateHandle,
    // ⭐ 수정: initialVoteId 파라미터 삭제 (SavedStateHandle에서 직접 읽음)
    private val voteRepository: VoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val TAG = "VoteViewModel"

    // ⭐ Navigation Argument에서 "voteId"를 읽어와 Long으로 변환합니다.
    private val currentVoteId: Long = savedStateHandle.get<String>("voteId")?.toLongOrNull() ?: -1L

    // UI 상태 관리
    private val _uiState = MutableStateFlow(VoteUiState(voteId = currentVoteId))
    val uiState: StateFlow<VoteUiState> = _uiState.asStateFlow()

    // 이벤트 스트림 (내비게이션 처리용)
    private val _events = Channel<VoteUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        // ⭐ 안전성 강화: voteId가 유효한 경우에만 데이터 로딩을 시도합니다.
        if (currentVoteId > 0) {
            loadVoteDetails(currentVoteId)
        } else {
            // 유효하지 않은 ID일 경우, 에러 메시지를 표시하고 API 호출을 막습니다.
            _uiState.update { it.copy(errorMessage = "네비게이션 오류: 유효한 투표 ID를 찾을 수 없습니다.") }
        }
    }

    // -------------------------------------------------------------------
    /**
     * 투표 ID에 해당하는 투표 항목 목록 (투표율 포함)을 Repository를 통해 비동기로 불러오는 함수
     */
    fun loadVoteDetails(voteId: Long) {
        if (voteId <= 0) {
            _uiState.update { it.copy(errorMessage = "유효하지 않은 투표 ID입니다.") }
            return
        }

        viewModelScope.launch {
            // 1. 로딩 상태 시작
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 2. Repository 호출 (GET /vote/{voteId} 호출)
            val result = voteRepository.getVoteDetail(voteId)

            // 3. Kotlin 표준 Result<T> 처리
            result.onSuccess { voteDetail ->
                // API 응답 DTO (VoteItem)를 UI에서 사용할 OptionItem으로 매핑
                val options = voteDetail.items.mapIndexed { index, voteItem ->
                    // VoteItem.voteRate (투표율 0~100)을 OptionItem.currentVotes에 할당
                    OptionItem(
                        id = index, // 임시 ID
                        title = voteItem.name,
                        currentVotes = voteItem.voteRate // 투표율(%) 반영
                    )
                }

                Log.d(TAG, "투표 상세 정보 로드 성공: ${options.size}개 항목")

                // 성공 시: 데이터 업데이트 및 로딩 종료
                _uiState.update {
                    it.copy(
                        options = options,
                        isLoading = false,
                        title = voteDetail.title // ⭐ 투표 제목 저장
                    )
                }
            }.onFailure { throwable ->
                // 실패 시: 에러 메시지 업데이트 및 로딩 종료
                val errorMessage = throwable.message ?: "알 수 없는 오류로 투표 상세 정보를 불러오는 데 실패했습니다."
                Log.e(TAG, "투표 상세 정보 로드 실패", throwable)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------

    /**
     * 투표 처리 함수: 선택된 항목을 서버에 전송합니다.
     * @param selectedOptionId 투표된 항목의 로컬 ID
     */
    fun vote(selectedOptionId: Int?) {
        if (selectedOptionId == null) return

        viewModelScope.launch {
            // 1. 선택된 항목 및 투표 ID 유효성 검사
            val selectedItem = _uiState.value.options.find { it.id == selectedOptionId }
            val currentVoteId = _uiState.value.voteId

            if (selectedItem == null || currentVoteId <= 0) {
                _uiState.update { it.copy(errorMessage = "투표 정보가 유효하지 않습니다.") }
                return@launch
            }

            // 2. AuthRepository에서 현재 사용자 ID를 가져옵니다.
            val currentUserId = authRepository.getCurrentUserId()

            if (currentUserId == null || currentUserId <= 0) {
                _uiState.update { it.copy(errorMessage = "로그인이 필요하거나 유효한 사용자 ID를 찾을 수 없습니다.") }
                return@launch
            }

            // 3. 로딩 상태 시작
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 4. 투표 API 호출 로직 (VoteRepository에 selectVote 함수가 있다고 가정)
            // ⭐ 수정: Long 타입 그대로 전달하도록 변경 (Repository 함수도 Long을 받는다고 가정)
            val result = voteRepository.selectVote(
                voteId = currentVoteId, // Long 타입 그대로 사용
                selectedOptionName = selectedItem.title, // 항목의 이름으로 투표한다고 가정
                userId = currentUserId // ⭐ 가져온 사용자 ID 전달
            )

            result.onSuccess {
                Log.d(TAG, "투표 전송 성공: ${selectedItem.title}")

                // 5. UI 상태 업데이트 및 이벤트 전송
                // 투표 성공 후에는 서버에서 최신 투표 현황을 다시 받아와 UI가 즉시 업데이트됩니다.
                loadVoteDetails(currentVoteId) // ⭐ 재로딩 추가 (UI 업데이트)

                // ⭐ 투표 완료 후 돌아가기 이벤트를 보냄 (크래시 방지)
                _events.send(VoteUiEvent.NavigateToVoteClear)

            }.onFailure { throwable ->
                // 실패 시: 에러 메시지 업데이트 및 로딩 종료
                val errorMessage = throwable.message ?: "투표 전송에 실패했습니다."
                Log.e(TAG, "투표 전송 실패", throwable)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    // 4. Back 버튼 클릭 이벤트 함수 추가
    fun onBackButtonClicked() {
        viewModelScope.launch {
            _events.send(VoteUiEvent.NavigateToBack)
        }
    }

    // 5. 룰렛 시작 버튼 클릭 함수 (MyVoteScreen에서 사용)
    fun onRouletteStartClicked() {
        // 투표 결과를 기반으로 룰렛을 시작하는 로직
        viewModelScope.launch {
            _events.send(VoteUiEvent.NavigateToRoulette)
        }
    }

    // -------------------------------------------------------------------
    // ⭐ Factory 추가: SavedStateHandle 및 의존성을 안전하게 제공
    companion object {
        fun provideFactory(
            voteRepository: VoteRepository,
            authRepository: AuthRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: androidx.lifecycle.viewmodel.CreationExtras // SavedStateHandle을 가져오기 위해 필요
            ): T {
                // SavedStateHandle을 가져옵니다.
                val savedStateHandle = extras.createSavedStateHandle()

                // VoteViewModel 인스턴스를 생성하고 의존성을 주입합니다.
                return VoteViewModel(
                    savedStateHandle = savedStateHandle,
                    voteRepository = voteRepository,
                    authRepository = authRepository
                ) as T
            }
        }
    }
   }