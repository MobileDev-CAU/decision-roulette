package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.RouletteItem
import org.springframework.data.jpa.repository.JpaRepository

interface RouletteItemRepository : JpaRepository<RouletteItem, Long>{
    fun findByName(name: String): List<RouletteItem>

}