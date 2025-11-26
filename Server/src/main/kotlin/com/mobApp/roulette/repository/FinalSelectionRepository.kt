package com.mobApp.roulette.repository

import com.mobApp.roulette.domain.FinalSelection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface FinalSelectionRepository : JpaRepository<FinalSelection, Long> {
    fun findAllByUserId(userId: Long): List<FinalSelection>
    fun findAllByRouletteId(rouletteId: Long): List<FinalSelection>

    /*@Query("""
        SELECT fs.finalChoice AS name, COUNT(fs.finalChoice) AS cnt
        FROM FinalSelection fs
        WHERE fs.rouletteId = :rouletteId
        GROUP BY fs.finalChoice
        ORDER BY cnt DESC
    """)
    fun findTopChoices(@Param("rouletteId") rouletteId: Long): List<Map<String, Any>>*/
}