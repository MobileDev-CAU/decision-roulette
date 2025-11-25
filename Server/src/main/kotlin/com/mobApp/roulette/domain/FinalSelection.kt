package com.mobApp.roulette.domain

import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "final_selections")
data class FinalSelection(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        val userId: Long? = null,

        val rouletteId: Long,

        val rouletteResultItem: String,

        val finalChosenItem: String,

        val wasFollowingResult: Boolean = false,

        val createdAt: Instant = Instant.now()
)