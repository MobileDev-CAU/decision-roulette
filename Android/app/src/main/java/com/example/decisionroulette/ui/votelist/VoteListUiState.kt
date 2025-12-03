package com.example.decisionroulette.ui.votelist

import com.example.decisionroulette.api.vote.VoteListItem


data class VoteListState(
    val isLoading: Boolean = false,
    val voteItems: List<VoteItemUiModel> = emptyList(),
    val errorMessage: String? = null
)

// VoteItemUiModel.kt (UI에 필요한 정보를 담는 모델)
data class VoteItemUiModel(
    val voteId: Long,
    val userNickname: String,
    val title: String,
    val isMyVote: Boolean,
    val itemCount: Int,
)

