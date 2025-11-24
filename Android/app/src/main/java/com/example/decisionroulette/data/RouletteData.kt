package com.example.decisionroulette.data

data class RouletteItem(
    val name: String,
    val weight: Float = 1.0f // 기본값 1.0 (비율이 같을 때 사용)
)

// 1. 룰렛 생성 요청
data class RouletteCreateRequest(
    val title: String,
    val items: List<String>,
    val ownerId: Int
)

// 2. 룰렛 상세 조회 응답
data class RouletteDetailResponse(
    val rouletteId: Int,
    val title: String,
    val items: List<String>
)

// 3. 룰렛 스핀 결과
data class RouletteSpinResponse(
    val result: String
)

// 4. 최종 선택 저장 요청
data class FinalChoiceRequest(
    val rouletteId: Int,
    val spinResult: String, // 룰렛이 골라준 거
    val finalChoice: String // 사용자가 진짜 먹은 거 (같을 수도, 다를 수도 있음)
)