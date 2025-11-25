package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.SpinHistory
import org.springframework.data.jpa.repository.JpaRepository

interface SpinHistoryRepository : JpaRepository<SpinHistory, Long> {
    fun findAllByUserId(userId: Long): List<SpinHistory>
    fun findAllByRouletteId(rouletteId: Long): List<SpinHistory>
}