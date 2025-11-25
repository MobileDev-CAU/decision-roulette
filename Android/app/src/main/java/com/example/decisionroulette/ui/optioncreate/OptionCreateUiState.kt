package com.example.decisionroulette.ui.optioncreate

data class Option(
    val id: Int,
    val value: String = ""
)

data class OptionCreateUiState(
    val topicTitle: String = "점심 메뉴", // ------> 이거도 데이터 연결할때 수정
    val options: List<Option> = emptyList()
)
