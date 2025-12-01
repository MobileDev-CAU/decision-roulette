package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.Roulette
import org.springframework.data.jpa.repository.JpaRepository

interface  RouletteRepository : JpaRepository<Roulette, Long> {
    fun findAllByOwnerId(ownerId: Long): List<Roulette>
}