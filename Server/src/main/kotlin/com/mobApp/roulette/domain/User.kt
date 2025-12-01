package com.mobApp.roulette.domain

import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false, unique = true)
        var userId: String = "",   // ★ 기본값 추가

        @Column(nullable = false)
        var passwordHash: String = "",

        @Column(nullable = false)
        var nickname: String = "",

        @Column(nullable = false)
        var provider: String = "local",

        @Column(nullable = false)
        var createdAt: Instant = Instant.now()
) {
        // ★★★ JPA가 필요한 기본 생성자
        constructor() : this(
                id = null,
                userId = "",
                passwordHash = "",
                nickname = "",
                provider = "local",
                createdAt = Instant.now()
        )
}
