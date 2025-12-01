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
}