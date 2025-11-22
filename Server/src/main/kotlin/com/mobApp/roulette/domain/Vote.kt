package com.mobApp.roulette.domain

import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "votes")
data class Vote(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(nullable = false)
        var title: String,

        val createdByUserId: Long? = null,

        val createdAt: Instant = Instant.now(),

        @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
        @JoinColumn(name = "vote_id")
        var options: MutableList<VoteOption> = mutableListOf()
)