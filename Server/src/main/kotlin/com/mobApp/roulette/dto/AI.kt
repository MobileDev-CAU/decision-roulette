package com.mobApp.roulette.dto

data class AIRecommendRequest(
        val title: String,
        val history: List<String>?,
        val popular: List<String>?
)
data class AIRecommendResponse(
        val recommendations: String
)
data class AIWarningResponse(
        val warning: String
)