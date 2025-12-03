package com.example.decisionroulette.ui.optioncreate

data class Option(
    val id: Int,
    val value: String = ""
)

data class OptionCreateUiState(
    val topicTitle: String = "점심 메뉴",
    val options: List<Option> = emptyList(),
    val isLoading: Boolean = false,

    val showAiDialog: Boolean = false,
    val aiRecommendations: List<String> = emptyList()
)
