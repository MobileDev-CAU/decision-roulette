package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByUserId(userId: String): Optional<User>
    fun existsByUserId(userId: String): Boolean
}