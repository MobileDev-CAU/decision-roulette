package com.mobApp.roulette

import com.mobApp.roulette.dto.AIRecommendRequest
import com.mobApp.roulette.service.AIService
import org.junit.jupiter.api.Test

class AIServiceTest {

    @Test
    fun `AI 기능만 테스트`() {
        // 1. 서버 없이 그냥 내가 직접 서비스(부품)를 만듭니다.
        val myService = AIService()

        // 2. 가짜 질문 데이터를 만듭니다.
        val req = AIRecommendRequest(
            title = "Date spot",
            history = listOf("Amusement park"),
            popular = listOf("movie theater")
        )

        println("============== AI에게 질문 전송 중... ==============")

        // 3. 실행 (DB 연결 안 하고 바로 Gemini한테 갑니다)
        val result = myService.recommend(req)

        println("============== 도착한 답변 ==============")
        println(result)
        // 결과가 객체로 나오면 result.result 또는 result.keyword 처럼 찍어보세요
    }
}