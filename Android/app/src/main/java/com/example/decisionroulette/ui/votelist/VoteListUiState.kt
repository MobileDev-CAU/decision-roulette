package com.example.decisionroulette.ui.votelist



data class VoteListState(
    val isLoading: Boolean = false,
    val voteItems: List<VoteItemUiModel> = emptyList(),
    val errorMessage: String? = null
)

data class VoteItemUiModel(
    val voteId: Long,
    val userNickname: String,
    val title: String,
    val isMyVote: Boolean,
    val itemCount: Int,
)

