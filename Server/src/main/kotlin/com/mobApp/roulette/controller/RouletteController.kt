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

    @GetMapping("/{id}/ai-waring") ///ai수정필요
    fun aiWarning(@PathVariable id: Long): ResponseEntity<AIWarningResponse> =
        ResponseEntity.ok(aiService.warning())
    @GetMapping("/{id}/spin")
    fun spin(
            @PathVariable id: Long,
            @RequestParam userId: Long
    ):ResponseEntity<RouletteSpinResponse> =
            ResponseEntity.ok(rouletteService.spin(id, userId))
    @PostMapping("/ai/recommend")
    fun recommend(@RequestBody req: AIRecommendRequest): ResponseEntity<AIRecommendResponse> =
            ResponseEntity.ok(aiService.recommend(req))
    @GetMapping("{Id}")
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