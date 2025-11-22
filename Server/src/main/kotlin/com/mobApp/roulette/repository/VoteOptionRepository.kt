package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.VoteOption
import org.springframework.data.jpa.repository.JpaRepository

interface VoteOptionRepository : JpaRepository<VoteOption, Long>