package com.mobApp.roulette.domain

import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "spin_histories")
data class SpinHistory(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        val userId: Long? = null,

        val rouletteId: Long,

        val selectedItemId: Long,

        val selectedItemName: String,

        val createdAt: Instant = Instant.now(),

        @Column(length = 2000)
        val metadata: String? = null
)