package com.mobApp.roulette.dto

data class VoteListItemResponse(
        val voteId: Long,
        val title: String,
        val itemCount: Int
)
data class VoteDetailItem(
        val name: String,
        val voteRate: Double
)

data class VoteDetailResponse(
        val voteId: Long,
        val title: String,
        val items: List<VoteDetailItem>
)
data class VoteRequest(
        val itemName: String
)
data class VoteResponse(
        val message: String
)
data class VoteToRouletteItem( //투표비율을 룰렛으로
        val name: String,
        val weight: Double
)
data class VoteToRouletteResponse(
        val rouletteId: Long,
        val title: String,
        val items: List<VoteToRouletteItem>
)
data class VoteUploadRequest( //룰렛을 투표로 업로드
        val rouletteId: Long
)
data class VoteUploadResponse(
        val voteId: Long,
        val message: String
)