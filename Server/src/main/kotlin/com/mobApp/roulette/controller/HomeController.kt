package com.mobApp.roulette.controller

import com.mobApp.roulette.dto.*
import com.mobApp.roulette.service.HomeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/home")
class HomeController(
        private val homeService: HomeService
){
    @GetMapping("/stats")
    fun stats(): ResponseEntity<HomeStatsResponse> =
    ResponseEntity.ok(homeService.getHomeState())
}