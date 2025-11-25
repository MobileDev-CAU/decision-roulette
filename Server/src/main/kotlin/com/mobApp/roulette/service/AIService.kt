package com.mobApp.roulette.service

import com.mobApp.roulette.dto.AIRecommendRequest
import com.mobApp.roulette.dto.AIRecommendResponse
import com.mobApp.roulette.dto.AIWarningResponse
import org.springframework.stereotype.Service

@Service
class AIService{
    fun recommend(req: AIRecommendRequest): AIRecommendResponse{
        val title = req.title
        val recs = listOf("$title 1", "$title 2", "$title 3") //실제 AI연동하기
        return AIRecommendResponse(recommendations = recs)
    }
    fun warning(): AIWarningResponse {
        return AIWarningResponse("AI 연결하기")
    }
}