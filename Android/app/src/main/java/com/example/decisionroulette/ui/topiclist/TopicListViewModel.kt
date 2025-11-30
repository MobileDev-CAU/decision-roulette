//package com.example.decisionroulette.ui.topiclist
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.State
//import com.example.decisionroulette.data.topiclist.RouletteList
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.receiveAsFlow
//import kotlinx.coroutines.launch
//
//
//sealed interface TopicListUiEvent {
//    object NavigateToAddTopic : TopicListUiEvent
//}
//
//// 임시 데이터
//private val initialDummyTopics = listOf(
//    RouletteList(
//        rouletteId = 10,
//        title = "점심 메뉴",
//        itemCount = 3
//    ),
//    RouletteList(
//        rouletteId = 20,
//        title = "오늘 할 일",
//        itemCount = 2
//    ),
//    RouletteList(
//        rouletteId = 30,
//        title = "입을 옷",
//        itemCount = 4
//    )
//)
//
//
//class TopicListViewModel : ViewModel() {
//
//
//    var uiState by mutableStateOf(TopicListUiState(isLoading = true))
//        private set
//
//    private val _events = Channel<TopicListUiEvent>()
//    val events = _events.receiveAsFlow()
//
//
//    private val _menuOpenTopicId = mutableStateOf<Int?>(null)
//    val menuOpenTopicId: State<Int?> = _menuOpenTopicId
//
//    init {
//        loadTopics()
//    }
//    private fun loadTopics() {
//        uiState = uiState.copy(topics = initialDummyTopics, isLoading = false)
//    }
//
//    // 주제 생성화면으로 전환
//    fun onAddListButtonClicked() {
//        viewModelScope.launch {
//            _events.send(TopicListUiEvent.NavigateToAddTopic)
//        }
//    }
//
//    // > 버튼 누르면 그 주제 룰렛 화면으로 전환 -------------> 이거 데이터 이용할때 수정해야함
//    fun onTopicSelected(topic: RouletteList) {
//        println("Topic Selected: ${topic.title}")
//    }
//
//    // : 버튼 누르면
//    fun onMoreOptionsSelected(topic: RouletteList) {
//        _menuOpenTopicId.value = if (_menuOpenTopicId.value == topic.rouletteId) null else topic.rouletteId
//    }
//
//    // 삭제 버튼 누르면
//    fun onDeleteConfirmed(topic: RouletteList) {
//
//        _menuOpenTopicId.value = null
//
//        val updatedList = uiState.topics.filter { it.rouletteId != topic.rouletteId }
//        uiState = uiState.copy(topics = updatedList)
//        println("Topic Deleted (Local): ${topic.title}")
//    }
//
//    fun dismissMenu() {
//        _menuOpenTopicId.value = null
//    }
//}