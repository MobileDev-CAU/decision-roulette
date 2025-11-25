package com.mobApp.roulette.service

import com.mobApp.roulette.dto.HomeStatsResponse
import org.springframework.stereotype.Service

@Service
class HomeService {
    fun getHomeState(): HomeStatsResponse{
        val topTopics = listOf("점심", "데이트", "간식") //DB로할건지 AI쓸건지 상의
        return HomeStatsResponse(topTopics)
    }
}