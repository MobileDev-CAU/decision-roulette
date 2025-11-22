package com.mobApp.roulette.domain

import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false, unique = true)
        var userId: String,

        @Column(nullable = false)
        var passwordHash: String,

        @Column(nullable = false)
        var nickname: String,

        @Column(nullable = false)
        var provider: String = "local", // 로컬/구글 등

        @Column(nullable = false)
        var createdAt: Instant = Instant.now()
)
