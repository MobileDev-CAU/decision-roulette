package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.VoteRecord
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRecordRepository : JpaRepository<VoteRecord, Long> {
    fun existsByVoteIdAndUserId(voteId: Long, userId: Long): Boolean
}