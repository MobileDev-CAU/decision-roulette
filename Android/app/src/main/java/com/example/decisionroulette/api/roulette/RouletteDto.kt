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