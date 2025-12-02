package com.example.decisionroulette.ui.optioncreate

data class Option(
    val id: Int,
    val value: String = ""
)

data class OptionCreateUiState(
    val topicTitle: String = "점심 메뉴", // TODO: 앞 화면에서 전달받은 제목으로 설정 필요
    val options: List<Option> = emptyList(),
    val isLoading: Boolean = false
)
