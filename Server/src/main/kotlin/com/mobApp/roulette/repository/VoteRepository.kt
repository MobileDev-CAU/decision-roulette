package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<Vote, Long>