package com.mobApp.roulette.controller

import com.mobApp.roulette.dto.*
import com.mobApp.roulette.service.HomeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class HomeController(
        private val homeService: HomeService
){
    @GetMapping("/")
    fun root(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "서버 정상 실행 중"))
    }
    @GetMapping("/home/stats")
    fun stats(): ResponseEntity<HomeStatsResponse> =
    ResponseEntity.ok(homeService.getHomeState())
}