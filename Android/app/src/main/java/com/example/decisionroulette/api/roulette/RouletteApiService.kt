package com.example.decisionroulette.api.roulette

import retrofit2.http.*

interface RouletteApiService {

    // 룰렛 리스트 조회
    @GET("roulette/list")
    suspend fun getRouletteList(
        @Query("ownerId") ownerId: Int
    ): List<RouletteDto>

    // 룰렛 생성
    @POST("roulette")
    suspend fun createRoulette(
        @Body request: RouletteCreateRequest
    ): RouletteCreateResponse

    // 룰렛 삭제
    @DELETE("roulette/{id}")
    suspend fun deleteRoulette(
        @Path("id") id: Int
    ): RouletteDeleteResponse

    // 룰렛 상세 조회
    @GET("roulette/{id}")
    suspend fun getRouletteDetail(
        @Path("id") id: Int
    ): RouletteDetailResponse

    // 룰렛 항목 이름 수정
    @PUT("roulette/{id}/item/{itemId}")
    suspend fun updateRouletteItem(
        @Path("id") rouletteId: Int,
        @Path("itemId") itemId: Int,
        @Body request: UpdateItemRequest // {"newItemName": "..."}
    ): UpdateItemResponse

    // 최종 결과 저장
    @POST("roulette/result/final-choice")
    suspend fun saveFinalChoice(
        @Query("userId") userId: Int,
        @Body request: FinalChoiceRequest
    ): FinalChoiceResponse

    // 룰렛 만족도 피드백 전송
    @POST("roulette/result/feedback")
    suspend fun saveFeedback(
        @Query("userId") userId: Int,
        @Body request: RouletteFeedbackRequest
    ): RouletteFeedbackResponse

    // AI 항목 추천
    @POST("roulette/ai/recommend")
    suspend fun getAiRecommendation(
        @Query("userId") userId: Int,
        @Body request: AiRecommendRequest
    ): AiRecommendResponse

    // AI 분석 리포트
    @POST("roulette/ai/analyze")
    suspend fun analyzeRoulette(
        @Body request: AiAnalyzeRequest
    ): AiAnalyzeResponse
}