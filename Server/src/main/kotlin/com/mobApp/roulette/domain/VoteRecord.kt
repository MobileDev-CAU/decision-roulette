package com.mobApp.roulette.domain

import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "vote_records", uniqueConstraints = [
    UniqueConstraint(columnNames = ["voteId", "userId"])
])
data class VoteRecord(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        val voteId: Long,

        val userId: Long? = null,

        val optionId: Long,

        val createdAt: Instant = Instant.now()
)
