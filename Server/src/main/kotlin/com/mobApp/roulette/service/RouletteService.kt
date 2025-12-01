package com.mobApp.roulette.service

import com.mobApp.roulette.domain.Roulette
import com.mobApp.roulette.domain.RouletteItem
import com.mobApp.roulette.dto.*
import com.mobApp.roulette.repository.RouletteRepository
import com.mobApp.roulette.repository.RouletteItemRepository
import com.mobApp.roulette.repository.SpinHistoryRepository
import com.mobApp.roulette.domain.SpinHistory
import com.mobApp.roulette.repository.FinalSelectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.random.Random

@Service
class RouletteService(
        private val rouletteRepository: RouletteRepository,
        private val rouletteItemRepository: RouletteItemRepository,
        private val spinHistoryRepository: SpinHistoryRepository,
        private val finalSelectionRepository: FinalSelectionRepository
){
    @Transactional
    fun createRoulette(req: CreateRouletteRequest):RouletteResponse{
        val items= req.items.mapIndexed {
            idx, name -> RouletteItem(name = name, orderIndex = idx, weight = 1.0)
        }.toMutableList()
        val roulette = Roulette(
                ownerId = req.ownerId,
                title = req.title,
                items = items
        )
        val saved = rouletteRepository.save(roulette)
        return saved.toRouletteResponse()
    }
    fun listRoulettes(): List<RouletteListItemResponse> = //이게 필요할까?
            rouletteRepository.findAll().map {RouletteListItemResponse(it.id!!, it.title, it.items.size)}
    fun listByOwner(ownerId: Long):List<RouletteListItemResponse> =
            rouletteRepository.findAllByOwnerId(ownerId).map{RouletteListItemResponse(it.id!!, it.title, it.items.size)}
    fun getDetail(id: Long): RouletteDetailResponse{
        val r = rouletteRepository.findById(id).orElseThrow{ IllegalArgumentException("존재하지 않는 룰렛") }
        val itemResponses = r.items.map {
            RouletteDetailItemResponse(
                    itemId = it.id!!,
                    name = it.name,
                    orderIndex = it.orderIndex,
                    weight = it.weight
            )
        }
        return RouletteDetailResponse(
                rouletteId = r.id!!,
                title = r.title,
                items = itemResponses
        )
    }
    @Transactional
    fun spin(rouletteId: Long, userId:Long): RouletteSpinResponse{
        val r = rouletteRepository.findById(rouletteId).orElseThrow { IllegalArgumentException("존재하지 않는 룰렛") }
        if (r.items.isEmpty()) throw IllegalStateException("룰렛에 아이템이 없습니다.")
        val totalWeight = r.items.map{it.weight}.sum()
        val rnd = Random.nextDouble() *totalWeight
        var accum = 0.0
        var chosen: RouletteItem = r.items.first()
        for(it in r.items){
            accum += it.weight
            if (rnd <= accum){
                chosen = it
                break
            }
        }
        val hist = SpinHistory(
                userId = userId,
                rouletteId = r.id!!,
                selectedItemId = chosen.id!!,
                selectedItemName = chosen.name,
                createdAt = Instant.now()
        )
        spinHistoryRepository.save(hist)
        return RouletteSpinResponse(chosen.name)
    }
    @Transactional
    fun deleteRoulette(rouletteId: Long){
        val roulette = rouletteRepository.findById(rouletteId).orElseThrow { IllegalArgumentException("존재하지 않는 룰렛입니다.") }
        rouletteRepository.delete(roulette)
    }
    @Transactional
    fun updateRouletteItem(
            rouletteId: Long,
            itemId: Long,
            req: UpdateRouletteRequest
    ): UpdateRouletteResponse{
        val roulette = rouletteRepository.findById(rouletteId).orElseThrow { IllegalArgumentException("존재하지 않는 룰렛입니다.") }
        val item = roulette.items.firstOrNull{it.id == itemId}?: throw IllegalArgumentException("해당 아이템이 존재하지 않습니다.")
        item.name = req.newItemName
        val saved = rouletteRepository.save(roulette)
        return UpdateRouletteResponse(
                success = true,
                rouletteId= saved.id!!,
                items = saved.items.map{
                    RouletteDetailItemResponse(
                            itemId = it.id!!,
                            name = it.name,
                            orderIndex = it.orderIndex,
                            weight = it.weight
                    )
                }
        )
    }
    // 특정 주제의 모든 사용자의 최종 선택 조회 추가
    fun getPopularItemsByTitle(title: String, limit: Int = 3): List<String> {
        // 1. 제목이 유사한 룰렛들 찾기
        val keywords = title.split(" ").filter { it.isNotEmpty() }

        val roulettes = rouletteRepository.findAll()
            .filter { roulette ->
                // ✅ 키워드 중 하나라도 포함되면 매칭
                keywords.any { keyword ->
                    roulette.title.contains(keyword, ignoreCase = true)
                }
            }


        if (roulettes.isEmpty()) return emptyList()

        // 2. 해당 룰렛들의 ID 수집
        val rouletteIds = roulettes.mapNotNull { it.id }

        // 3. FinalSelection에서 가장 많이 선택된 항목 찾기
        val itemCounts = mutableMapOf<String, Int>()

        rouletteIds.forEach { rouletteId ->
            val selections = finalSelectionRepository.findAllByRouletteId(rouletteId)
            selections.forEach { selection ->
                val item = selection.finalChosenItem
                itemCounts[item] = itemCounts.getOrDefault(item, 0) + 1
            }
        }

        // 4. 상위 N개 반환
        return itemCounts.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }
    private fun Roulette.toRouletteResponse(): RouletteResponse =
            RouletteResponse(this.id!!, this.ownerId, this.title, this.items.map{it.toResponse()})
    private fun RouletteItem.toResponse(): RouletteDetailItemResponse =
            RouletteDetailItemResponse(this.id!!, this.name, this.orderIndex, this.weight)
}