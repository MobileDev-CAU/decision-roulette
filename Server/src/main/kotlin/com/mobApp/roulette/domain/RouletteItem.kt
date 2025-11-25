package com.mobApp.roulette.domain

import jakarta.persistence.*

@Entity
@Table(name = "roulette_items")
data class RouletteItem(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = false)
        var orderIndex: Int = 0,

        @Column(nullable = false)
        var weight: Double = 1.0, // 기본 가중치

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "roulette_id")
        var roulette: Roulette? = null

)