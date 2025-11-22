package com.mobApp.roulette.service

import com.mobApp.roulette.domain.Roulette
import com.mobApp.roulette.domain.RouletteItem
import com.mobApp.roulette.dto.*
import com.mobApp.roulette.repository.RouletteRepository
import com.mobApp.roulette.repository.RouletteItemRepository
import com.mobApp.roulette.repository.SpinHistoryRepository
import com.mobApp.roulette.domain.SpinHistory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.random.Random

@Service
class RouletteService(
        private val rouletteRepository: RouletteRepository,
        private val rouletteItemRepository: RouletteItemRepository,
        private val spinHistoryRepository: SpinHistoryRepository
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
        return RouletteDetailResponse(r.id!!, r.title, r.items.map{it.name})
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
                    RouletteItemResponse(
                            itemId = it.id!!,
                            name = it.name,
                            orderIndex = it.orderIndex,
                            weight = it.weight
                    )
                }
        )
    }
    private fun Roulette.toRouletteResponse(): RouletteResponse =
            RouletteResponse(this.id!!, this.ownerId, this.title, this.items.map{it.toResponse()})
    private fun RouletteItem.toResponse(): RouletteItemResponse =
            RouletteItemResponse(this.id!!, this.name, this.orderIndex, this.weight)
}