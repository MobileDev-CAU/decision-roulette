package com.example.decisionroulette.api.roulette

// 1. [요청] 룰렛 항목 추천 (/roulette/ai/recommend)
data class AiRecommendRequest(
    val title: String,
    val history: List<String> = emptyList(), // [""] 등 빈 값 가능
    val popular: List<String> = emptyList()
)

// [응답] 추천 결과
data class AiRecommendResponse(
    val recommendations: List<String>
)

// 2. [요청] 룰렛 분석 (/roulette/ai/analyze)
data class AiAnalyzeRequest(
    val title: String,
    val items: List<String>
)

// [응답] 분석 결과
data class AiAnalyzeResponse(
    val analysis: List<AiAnalysisItem>
)

data class AiAnalysisItem(
    val item: String,
    val pros: String,
    val cons: String
)
