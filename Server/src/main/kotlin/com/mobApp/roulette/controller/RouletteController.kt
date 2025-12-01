package com.mobApp.roulette.controller

import com.mobApp.roulette.dto.*
import com.mobApp.roulette.service.AIService
import com.mobApp.roulette.service.FeedbackService
import com.mobApp.roulette.service.RouletteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/roulette")
class RouletteController(
        private val rouletteService: RouletteService,
        private val feedbackService: FeedbackService,
        private val aiService: AIService
){
    @GetMapping("/list")
    fun list(@RequestParam ownerId: Long):ResponseEntity<List<RouletteListItemResponse>> =
            ResponseEntity.ok(rouletteService.listByOwner(ownerId))

    @PostMapping
    fun create(@RequestBody req: CreateRouletteRequest): ResponseEntity<RouletteResponse> =
            ResponseEntity.ok(rouletteService.createRoulette(req))

    @GetMapping("/{id}/spin")
    fun spin(
            @PathVariable id: Long,
            @RequestParam userId: Long
    ):ResponseEntity<RouletteSpinResponse> =
            ResponseEntity.ok(rouletteService.spin(id, userId))
    @PostMapping("/ai/recommend")
    fun recommend(
        @RequestBody req: AIRecommendRequest,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<AIRecommendResponse> {

        // 1. 사용자 이력 조회 (userId가 있는 경우에만)
        val userHistory = if (userId != null) {
            feedbackService.getUserFinalChoiceHistory(userId, 3)
        } else {
            emptyList()
        }

        // 2. 인기 항목 조회 (주제 기반)
        val popularItems = rouletteService.getPopularItemsByTitle(req.title, 3)

        // 3. 요청 DTO에 데이터 추가
        val enrichedRequest = AIRecommendRequest(
            title = req.title,
            history = userHistory,
            popular = popularItems
        )

        // 4. AI 서비스 호출
        val result = aiService.recommend(enrichedRequest)
        return ResponseEntity.ok(result)
    }
    @PostMapping("/ai/analyze")
    fun analyze(@RequestBody req: AIAnalyzeRequest): ResponseEntity<AIAnalysisResponse> =
        ResponseEntity.ok(aiService.analyzeItems(req))
    @GetMapping("{id}")
    fun detail(@PathVariable id : Long): ResponseEntity<RouletteDetailResponse> =
            ResponseEntity.ok(rouletteService.getDetail(id))
    @PostMapping("/result/feedback")
    fun saveFeedback(
            @RequestBody req: FeedbackRequest,
            @RequestParam userId: Long
    ):ResponseEntity<FeedbackResponse> =
            ResponseEntity.ok(feedbackService.submitFeedback(req, userId))
    @PostMapping("/result/final-choice")
    fun finalChoice(
            @RequestBody req: FinalChoiceRequest,
            @RequestParam userId: Long
    ): ResponseEntity<FinalChoiceResponse> =
            ResponseEntity.ok(feedbackService.recordFinalChoice(req, userId))
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        rouletteService.deleteRoulette(id)
        return ResponseEntity.ok(mapOf("success" to true, "rouletteId" to id))
    }
    @PutMapping("/{id}/item/{itemId}")
    fun updateItem(
            @PathVariable id: Long,
            @PathVariable itemId:Long,
            @RequestBody req: UpdateRouletteRequest
    ): ResponseEntity<UpdateRouletteResponse>{
        val result = rouletteService.updateRouletteItem(id, itemId, req)
        return ResponseEntity.ok(result)
    }
}