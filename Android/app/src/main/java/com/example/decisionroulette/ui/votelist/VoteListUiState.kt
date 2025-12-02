package com.example.decisionroulette.ui.votelist

import com.example.decisionroulette.api.vote.VoteListItem


data class VoteItem(
    val id: String, // 각 투표를 식별하는 ID (필수)
    val title: String, // 투표 제목 (예: "주말 데이트 장소")
    val options: List<VoteOption>, // 투표 선택지 리스트

)

data class VoteOption(
    val name: String,
    val percentage: Int
)

data class VoteListState(
    val isLoading: Boolean = false,
    val voteItems: List<VoteListItem> = emptyList(),
    val errorMessage: String? = null
)

