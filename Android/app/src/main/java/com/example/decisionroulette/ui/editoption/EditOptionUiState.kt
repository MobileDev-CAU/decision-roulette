package com.example.decisionroulette.ui.editoption

import com.example.decisionroulette.ui.optioncreate.Option

data class EditOptionUiState(
    val rouletteId: Int = 0,
    val topicTitle: String = "",
    val options: List<Option> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
