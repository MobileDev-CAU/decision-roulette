package com.mobApp.roulette.domain

import jakarta.persistence.*

@Entity
@Table(name = "vote_options")
data class VoteOption(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = false)
        var voteCount: Long = 0
)