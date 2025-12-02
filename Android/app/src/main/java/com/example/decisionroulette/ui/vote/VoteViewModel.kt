package com.example.decisionroulette.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.data.repository.VoteRepository
import com.example.decisionroulette.api.auth.AuthRepository // AuthRepository import 추가
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log // 로깅 추가

// UI에서 사용할 항목 모델 정의
// 실제 API에서 받은 DTO를 UI 상태에 맞게 매핑할 수 있도록 정의합니다.
data class OptionItem(val id: Int, val title: String, val currentVotes: Int = 0)

// 투표 화면의 UI 상태 정의
data class VoteUiState(
    val options: List<OptionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val voteId: Long = -1L // 현재 투표 화면이 다루는 투표 ID (Navigation Argument로 받아야 함)
)

sealed interface VoteUiEvent {
    object NavigateToBack : VoteUiEvent
    object NavigateToRoulette : VoteUiEvent
    object NavigateToVoteClear : VoteUiEvent
}

class VoteViewModel(
    // 1. Repository 주입
    private val voteRepository: VoteRepository = VoteRepository(),
    private val authRepository: AuthRepository = AuthRepository(), // ⭐ AuthRepository 주입
    // 실제 투표 ID는 Navigation Arguments로 받아야 하지만, 예시를 위해 임시로 설정
    initialVoteId: Long = 2L
) : ViewModel() {
    private val TAG = "VoteViewModel"

    // ⭐ currentUserId 하드코딩 변수 제거

    // UI 상태 관리
    private val _uiState = MutableStateFlow(VoteUiState(voteId = initialVoteId))
    val uiState: StateFlow<VoteUiState> = _uiState.asStateFlow()

    // 이벤트 스트림 (내비게이션 처리용)
    private val _events = Channel<VoteUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        // ViewModel 초기화 시, 투표 상세 정보를 로드합니다.
        loadVoteDetails(initialVoteId)
    }

    // -------------------------------------------------------------------
    // ⭐ 2. 룰렛 항목 로딩 함수를 투표 상세 정보 로딩 함수로 변경
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
                        isLoading = false
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

            // ⭐ 2. AuthRepository에서 현재 사용자 ID를 가져옵니다.
            // (AuthRepository.getCurrentUserId()가 suspend fun이며 Int?를 반환한다고 가정)
            val currentUserId = authRepository.getCurrentUserId()

            if (currentUserId == null || currentUserId <= 0) {
                _uiState.update { it.copy(errorMessage = "로그인이 필요하거나 유효한 사용자 ID를 찾을 수 없습니다.") }
                return@launch
            }

            // 3. 로딩 상태 시작
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 4. 투표 API 호출 로직 (VoteRepository에 selectVote 함수가 있다고 가정)
            val result = voteRepository.selectVote(
                voteId = currentVoteId,
                selectedOptionName = selectedItem.title, // 항목의 이름으로 투표한다고 가정
                userId = currentUserId // ⭐ 가져온 사용자 ID 전달
            )

            result.onSuccess {
                Log.d(TAG, "투표 전송 성공: ${selectedItem.title}")

                // 5. UI 상태 업데이트 (로딩 종료 및 투표 수 증가)
                // 투표 성공 후에는 서버에서 최신 투표 현황(voteRate)을 다시 받아오는 것이 가장 정확합니다.
                // 여기서는 간단하게 로딩 종료 및 성공 상태만 반영합니다.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }

                // 6. 투표가 성공했으므로, 룰렛 화면으로 이동 이벤트를 발생시킵니다.
                _events.send(VoteUiEvent.NavigateToRoulette)

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
}