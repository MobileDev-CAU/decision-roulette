package com.mobApp.roulette.dto

data class AIRecommendRequest(
        val title: String,
        val history: List<String>?,
        val popular: List<String>?
)
data class AIRecommendResponse(
        val recommendations: List<String>
)
data class AIWarningResponse(
        val warning: String
)
data class AIAnalyzeRequest(
        val title: String,
        val items: List<String>
)

data class AnalysisItem(
        val item: String, // 아이템 이름 (예: 피자)
        val pros: String, // 장점
        val cons: String  // 단점
)

data class AIAnalysisResponse(
        val analysis: List<AnalysisItem>
)