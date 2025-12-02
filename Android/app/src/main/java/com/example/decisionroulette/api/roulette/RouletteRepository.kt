package com.example.decisionroulette.api.roulette

import com.example.decisionroulette.api.RetrofitClient
import com.example.decisionroulette.api.roulette.RouletteApiService
import com.example.decisionroulette.api.roulette.RouletteDto

class RouletteRepository(
    private val api: RouletteApiService = RetrofitClient.instance
) {

    // 룰렛 리스트 가져오기
    suspend fun getRouletteList(ownerId: Int): Result<List<RouletteDto>> {
        return try {
            val response = api.getRouletteList(ownerId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 룰렛 생성
    suspend fun createRoulette(title: String, items: List<String>, ownerId: Int): Result<RouletteCreateResponse> {
        return try {
            // Request 객체 생성
            val request = RouletteCreateRequest(
                title = title,
                items = items,
                ownerId = ownerId
            )
            val response = api.createRoulette(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 룰렛 삭제
    suspend fun deleteRoulette(rouletteId: Int): Result<RouletteDeleteResponse> {
        return try {
            val response = api.deleteRoulette(rouletteId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 상세 조회
    suspend fun getRouletteDetail(id: Int): Result<RouletteDetailResponse> {
        return try {
            val response = api.getRouletteDetail(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 항목 수정
    suspend fun updateItemName(rouletteId: Int, itemId: Int, newName: String): Result<UpdateItemResponse> {
        return try {
            val request = UpdateItemRequest(newItemName = newName)
            val response = api.updateRouletteItem(rouletteId, itemId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 최종 선택 저장
    suspend fun saveFinalChoice(rouletteId: Int, spinResult: String, finalChosenItem: String, userId: Int): Result<FinalChoiceResponse> {
        return try {
            val request = FinalChoiceRequest(
                rouletteId = rouletteId,
                spinResult = spinResult,
                finalChosenItem = finalChosenItem
            )
            val response = api.saveFinalChoice(userId = userId, request = request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 피드백 전송 함수
    suspend fun saveFeedback(rouletteId: Int, spinResult: String, satisfied: Boolean, userId: Int): Result<RouletteFeedbackResponse> {
        return try {
            val request = RouletteFeedbackRequest(
                rouletteId = rouletteId,
                spinResult = spinResult,
                satisfied = satisfied
            )
            val response = api.saveFeedback(userId = userId, request = request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // AI 추천 받기
    suspend fun getAiRecommendation(title: String, userId: Int): Result<AiRecommendResponse> {
        return try {
            val request = AiRecommendRequest(
                title = title,
                history = listOf(""),
                popular = listOf("")
            )
            val response = api.getAiRecommendation(userId = userId, request = request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // AI 분석 받기
    suspend fun analyzeRoulette(title: String, items: List<String>): Result<AiAnalyzeResponse> {
        return try {
            val request = AiAnalyzeRequest(
                title = title,
                items = items
            )
            val response = api.analyzeRoulette(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}