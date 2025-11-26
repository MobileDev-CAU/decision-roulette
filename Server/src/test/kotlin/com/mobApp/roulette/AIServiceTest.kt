package com.mobApp.roulette

import com.mobApp.roulette.dto.AIRecommendRequest
import com.mobApp.roulette.service.AIService
import org.junit.jupiter.api.Test

class AIServiceTest {

    private val myApiKey = ""

    // 1.서비스(부품) 조립 - 스프링 없이 직접 만듭니다.
    private val myService = AIService(myApiKey)

    @Test
    fun `1_단어_추천_기능_테스트`() {

        // 2. 가짜 질문 데이터를 만듭니다.
        val req = AIRecommendRequest(
            title = "Date spot",
            history = listOf("amusement park"),
            popular = listOf("movie theater")
        )

        try {
            // 3. 실행
            val result = myService.recommend(req)

            println("✅ AI 추천 성공!")
            // [수정 포인트] 이제 결과가 리스트(recommendations)로 오니까 하나씩 출력
            result.recommendations.forEach { keyword ->
                println("- 추천 키워드: $keyword")
            }
        } catch (e: Exception) {
            println("❌ 에러 발생: ${e.message}")
        }
        println("======================================\n")
    }

    @Test
    fun `2_비교분석_리포트_테스트`() {
        println("========== [2. 비교 분석] 시작 ==========")

        // 분석하고 싶은 아이템 리스트 (가짜 데이터)
        val itemsToAnalyze = listOf("pizza", "hamburger", "pasta")

        try {
            // 우리가 아까 만든 analyzeItems 함수 호출
            val result = myService.analyzeItems(itemsToAnalyze)

            println("✅ AI 분석 성공!")

            // 결과가 리스트(AnalysisItem)로 오니까 반복문으로 출력
            result.analysis.forEach { item ->
                println("[ ${item.item} ]")
                println("  👍 장점: ${item.pros}")
                println("  👎 단점: ${item.cons}")
                println("-----------------------------")
            }
        } catch (e: Exception) {
            println("❌ 에러 발생: ${e.message}")
        }
        println("======================================")
    }
}