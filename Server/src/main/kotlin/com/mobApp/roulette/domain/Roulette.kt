package com.mobApp.roulette.domain

import java.time.Instant
import jakarta.persistence.*
@Entity
@Table(name = "roulettes")
data class Roulette(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var ownerId: Long,

        @Column(nullable = false)
        var title: String,

        @Column(nullable = false)
        var createdAt: Instant = Instant.now(),

        @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
        @JoinColumn(name = "roulette_id")
        var items: MutableList<RouletteItem> = mutableListOf()
)