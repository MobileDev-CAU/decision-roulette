package com.mobApp.roulette.dto

data class FeedbackRequest(
        val rouletteId: Long,
        val spinResult: String,
        val isSatisfied: Boolean
)
data class FeedbackResponse(
        val message: String
)
data class FinalChoiceRequest(
        val rouletteId: Long,
        val spinResult: String,
        val finalChoice: String
)
data class FinalChoiceResponse(
        val message: String
)