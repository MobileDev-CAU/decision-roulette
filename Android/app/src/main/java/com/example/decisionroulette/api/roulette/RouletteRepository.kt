package com.example.decisionroulette.api.roulette

import com.example.decisionroulette.api.RetrofitClient
import com.example.decisionroulette.api.roulette.RouletteApiService
import com.example.decisionroulette.api.roulette.RouletteDto
// RetrofitClient가 있는 패키지를 import 하세요 (예: com.example.decisionroulette.api.RetrofitClient)

class RouletteRepository(
    // 생성자에서 Service를 주입받게 만들면 나중에 테스트하기 좋습니다.
    // 기본값으로 RetrofitClient.instance를 넣어주면 호출할 때 편합니다.
    private val api: RouletteApiService = RetrofitClient.instance
) {

    // 룰렛 리스트 가져오기
    suspend fun getRouletteList(ownerId: Int): Result<List<RouletteDto>> {
        return try {
            val response = api.getRouletteList(ownerId)
            // 성공하면 Result.success로 감싸서 반환
            Result.success(response)
        } catch (e: Exception) {
            // 실패하면 Result.failure로 에러 반환 (네트워크 에러 등)
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
            // API 호출
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
}