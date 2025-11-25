package com.mobApp.roulette.dto

data class AIRecommendRequest(
        val title: String
)
data class AIRecommendResponse(
        val recommendations: List<String>
)
data class AIWarningResponse(
        val warning: String
)