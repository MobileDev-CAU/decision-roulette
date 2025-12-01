package com.example.decisionroulette.api.roulette

data class RouletteDto(
    val rouletteId: Int,
    val title: String,
    val itemCount: Int
)

// [요청] 룰렛 생성할 때 보내는 데이터
data class RouletteCreateRequest(
    val title: String,
    val items: List<String>, // ["치마", "목도리"]
    val ownerId: Int
)

// [응답] 룰렛 생성 성공 시 오는 데이터
data class RouletteCreateResponse(
    val rouletteId: Int,
    val ownerId: Int,
    val title: String,
    val items: List<RouletteItemDto>
)

// (생성 응답 안에 들어있는 아이템 상세 정보)
data class RouletteItemDto(
    val itemId: Int,
    val name: String,
    val orderIndex: Int,
    val weight: Int
)

// [응답] 룰렛 삭제 성공 시 오는 데이터
data class RouletteDeleteResponse(
    val success: Boolean,
    val rouletteId: Int
)

// [응답] 룰렛 상세 조회 (/roulette/{id})
data class RouletteDetailResponse(
    val rouletteId: Int,
    val title: String,
    val items: List<RouletteItemDto>
)

// [요청] 룰렛 항목 수정 (/roulette/{id}/item/{itemId})
data class UpdateItemRequest(
    val newItemName: String // 서버가 "newItemName"이라는 키를 원함
)

// [응답] 룰렛 항목 수정 결과
data class UpdateItemResponse(
    val success: Boolean,
    val rouletteId: Int,
    val items: List<RouletteItemDto> // 수정된 리스트를 다시 줌
)

// [요청] 최종 선택 저장 (/roulette/result/final-choice)
data class FinalChoiceRequest(
    val rouletteId: Int,
    val spinResult: String,
    val finalChosenItem: String
)

// [응답] 최종 선택 저장 결과
data class FinalChoiceResponse(
    val message: String // "저장 완료"
)