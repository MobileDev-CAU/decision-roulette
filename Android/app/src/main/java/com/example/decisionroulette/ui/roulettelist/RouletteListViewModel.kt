package com.example.decisionroulette.ui.roulettelist

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decisionroulette.data.topiclist.RouletteList // 수정: RouletteList를 사용합니다.
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.example.decisionroulette.api.roulette.RouletteRepository


sealed interface TopicCreateUiEvent {
    object NavigateToCreateOption : TopicCreateUiEvent // 옵션 생성 화면으로 이동 (itemCount == 0)
    object NavigateToRoulette : TopicCreateUiEvent     // 룰렛 화면으로 이동 (itemCount > 0)
    object NavigateToBack : TopicCreateUiEvent         // 이전 화면으로 이동
}

// 초기 더미 데이터 (실제로는 Repository를 통해 서버에서 로드됩니다)
private val initialExistingTopics = listOf(
    RouletteList(rouletteId = 10, title = "점심 메뉴", itemCount = 3),
    RouletteList(rouletteId = 20, title = "오늘 할 일", itemCount = 5),
    RouletteList(rouletteId = 30, title = "입을 옷", itemCount = 5)
)

class TopicCreateViewModel : ViewModel() {
    private val repository = RouletteRepository()

    // UI State는 외부에 노출되어 Composable에서 관찰
    var uiState by mutableStateOf(TopicCreateUiState())
        private set

    // UI Event는 일회성 Side Effect를 처리하기 위해 사용
    private val _events = Channel<TopicCreateUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // 현재 입력 필드의 값
    private val _currentInput = mutableStateOf("")
    val currentInput: State<String> = _currentInput

    // 새로 생성되는 주제의 임시 ID 카운터 (음수로 시작하여 기존 ID와 충돌 방지)
    private var tempIdCounter = -1

    var menuOpenTopicId = mutableStateOf<Int?>(null)
        private set

    init {
        loadExistingTopics()
    }

    private fun loadExistingTopics() {
        viewModelScope.launch {
            // 로딩 시작
            uiState = uiState.copy(isLoading = true)

            // TODO ownerId 하드코딩 삭제 필요
            val result = repository.getRouletteList(ownerId = 10)

            result.onSuccess { dtoList ->
                // DTO(RouletteDto) -> UI 모델(RouletteList) 변환
                val mappedTopics = dtoList.map { dto ->
                    RouletteList(
                        rouletteId = dto.rouletteId,
                        title = dto.title,
                        itemCount = dto.itemCount
                    )
                }

                // 성공 시 상태 업데이트
                uiState = uiState.copy(
                    existingTopics = mappedTopics,
                    isLoading = false,
                    error = null
                )
            }.onFailure { exception ->
                // 실패 시 에러 처리
                println("API 로드 실패: ${exception.message}")
                uiState = uiState.copy(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }


     //입력 필드 값 업데이트
    fun updateCurrentInput(newText: String) {
        _currentInput.value = newText
    }


     // Enter 키 입력 시 호출: 입력 값을 새 항목 버튼으로 추가합
    fun addTopicFromInput() {
        val topicTitle = _currentInput.value.trim()

        if (topicTitle.isNotEmpty()) {
            val newTopic = UserTopic(tempId = tempIdCounter, title = topicTitle)
            // 임시 ID 감소
            tempIdCounter--

            // 새로운 주제 목록 업데이트
            val updatedOptions = uiState.userCreatedTopics + newTopic

            uiState = uiState.copy(userCreatedTopics = updatedOptions)

            // 입력 필드 초기화
            _currentInput.value = ""
            // 새 주제를 추가하면 자동으로 선택
            toggleTopicSelection(newTopic.tempId)
        }
    }


     // 항목 선택/해제 로직 (단일 선택)
    fun toggleTopicSelection(topicId: Int) {
        val newSelectedId = if (uiState.selectedTopicId == topicId) null else topicId
        uiState = uiState.copy(selectedTopicId = newSelectedId)
    }

    // 🔥 [추가] 메뉴 열기
    fun onMoreOptionsSelected(topicId: Int) {
        menuOpenTopicId.value = topicId
    }

    // 🔥 [추가] 메뉴 닫기
    fun dismissMenu() {
        menuOpenTopicId.value = null
    }

    // 🔥 [추가] 토픽 삭제 로직
    fun deleteTopic(topicId: Int, isExisting: Boolean) {
        if (isExisting) {
            // 기존 토픽 삭제
            val updatedList = uiState.existingTopics.filter { it.rouletteId != topicId }
            uiState = uiState.copy(existingTopics = updatedList)
        } else {
            // 사용자 생성(임시) 토픽 삭제
            val updatedList = uiState.userCreatedTopics.filter { it.tempId != topicId }
            uiState = uiState.copy(userCreatedTopics = updatedList)
        }

        // 만약 삭제된 토픽이 선택된 상태였다면 선택 해제
        if (uiState.selectedTopicId == topicId) {
            uiState = uiState.copy(selectedTopicId = null)
        }

        dismissMenu() // 메뉴 닫기
    }

     // Choice 버튼 클릭 시 호출
    fun onChoiceButtonClicked() {
        val selectedId = uiState.selectedTopicId

        if (selectedId != null) {
            // 1. 기존 주제에서 찾기
            val existingTopic = uiState.existingTopics.find { it.rouletteId == selectedId }

            // 2. 사용자 생성 주제에서 찾기
            val userTopic = uiState.userCreatedTopics.find { it.tempId == selectedId }

            // 기존 주제가 있다면 itemCount를 사용하고, 새 주제라면 itemCount는 0으로 간주합니다.
            val itemCount = existingTopic?.itemCount ?: 0

            // 선택된 주제의 이름 (나중에 API 호출 등에 사용)
            val selectedTopicTitle = existingTopic?.title ?: userTopic?.title ?: "Unknown Topic"


            // TODO: 실제 앱에서는 여기서 선택된 주제를 저장하거나,
            // 옵션 생성/룰렛 화면으로 이동 시 해당 주제의 ID/제목을 전달해야 합니다.
            // 예를 들어: navigate(Screen.Roulette, selectedId)

            viewModelScope.launch {
                // 옵션이 있는 경우 (itemCount > 0): 룰렛 화면으로 이동
                if (itemCount > 0) {
                    _events.send(TopicCreateUiEvent.NavigateToRoulette)
                }
                // 옵션이 없는 경우 (itemCount == 0 또는 새 주제): 옵션 생성 화면으로 이동
                else {
                    _events.send(TopicCreateUiEvent.NavigateToCreateOption)
                }
            }
        } else {
            println("ERROR: No topic selected for option creation")
        }
    }

   // 백 버튼
    fun onBackButtonClicked() {
        viewModelScope.launch {
            _events.send(TopicCreateUiEvent.NavigateToBack)
        }
    }
}