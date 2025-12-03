package com.example.decisionroulette.api.vote

/**
 * GET /vote/list 응답의 항목 하나를 나타내는 데이터 클래스
 */
data class VoteListItem(
    val voteId: Long,
    val title: String,
    val itemCount: Int,
    val userNickname: String
)

/**
 * GET /vote/{voteId} 응답의 투표 항목 (Item)을 나타내는 데이터 클래스
 */
data class VoteItem(
    val name: String,
    val voteRate: Double // 백분율 (0 ~ 100)
)

/**
 * GET /vote/{voteId} 응답의 전체 상세 정보를 나타내는 데이터 클래스
 */
data class VoteDetail(
    val voteId: Long,
    val title: String,
    val items: List<VoteItem> // 항목 리스트를 포함합니다.
)

data class VoteUploadRequest(
    val rouletteId: Int
)

data class VoteUploadResponse(
    val voteId: Int,
    val message: String
)


data class VoteRouletteDetailResponse(
    val rouletteId: Int,
    val title: String,
    val items: List<RouletteItemDto>
)

data class RouletteItemDto(
    val name: String,
    val weight: Double
)

data class VoteSelectRequest(
    val itemName: String // 요청 본문에 선택된 항목의 이름만 포함
)

data class VoteSelectResponse(
    val message: String // 서버 응답: {"message": "OK"}
)