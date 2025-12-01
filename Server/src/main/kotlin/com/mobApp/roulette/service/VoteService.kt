package com.mobApp.roulette.service

import com.mobApp.roulette.domain.Vote
import com.mobApp.roulette.domain.VoteOption
import com.mobApp.roulette.domain.VoteRecord
import com.mobApp.roulette.dto.*
import com.mobApp.roulette.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

@Service
class VoteService(
        private val voteRepository: VoteRepository,
        private val voteOptionRepository: VoteOptionRepository,
        private val voteRecordRepository: VoteRecordRepository,
        private val rouletteRepository: RouletteRepository,
        private val userRepository: UserRepository
){
    @Transactional
    fun createVote(title: String, optionNames: List<String>, creatorId: Long?): Vote{
        val options = optionNames.map{VoteOption(name = it)}.toMutableList()
        val vote = Vote(title = title, createdByUserId = creatorId, options = options)
        return voteRepository.save(vote)
    }
    @Transactional
    fun castVote(voteId: Long, optionId:Long, userId: Long?): VoteResponse{
        if(userId != null && voteRecordRepository.existsByVoteIdAndUserId(voteId, userId)){
            throw IllegalArgumentException("이미 투표하였습니다.")
        }
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("투표가 없습니다.") }
        val option = vote.options.firstOrNull{it.id == optionId} ?: throw IllegalArgumentException("항목이 없습니다.")
        option.voteCount = option.voteCount + 1
        voteRepository.save(vote)
        voteRecordRepository.save(VoteRecord(voteId = voteId, userId = userId, optionId = optionId))
        return VoteResponse("OK")
    }
    fun getVoteDetail(voteId: Long) : VoteDetailResponse{
        val v = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("투표가 없습니다.") }
        val optionDetails = v.options.map {VoteDetailItem(it.name, if(v.options.sumOf{o -> o.voteCount}.toDouble() == 0.0) 0.0 else (it.voteCount.toDouble() / v.options.sumOf{o -> o.voteCount}.toDouble())*100.0)}
        return VoteDetailResponse(v.id!!, v.title, optionDetails)
    }
    @Transactional
    fun voteToRoultte(voteId: Long): VoteToRouletteResponse{
        val v = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("투표가 없습니다.") }
        val total = v.options.sumOf {it.voteCount.toDouble()}
        val items = v.options.map{
            val w = if(total == 0.0) 1.0 else(it.voteCount.toDouble() / total)* 100.0
            VoteToRouletteItem(it.name, w)
        }
        return VoteToRouletteResponse(rouletteId = 0L, title = v.title, items = items)
    }
    fun getAllVotes(): List<VoteListItemResponse>{
        val votes = voteRepository.findAll()
        return votes.map {vote ->
            val user = vote.createdByUserId?.let { userRepository.findById(it).orElse(null) }

            VoteListItemResponse(
                    voteId = vote.id!!,
                    title = vote.title,
                    itemCount = vote.options.size,
                    userNickname = user?.nickname
            )
        }
    }
    fun findOptionIdByName(voteId: Long, name: String): Long{
        val vote = voteRepository.findById(voteId).orElseThrow { IllegalArgumentException("투표가 없습니다.") }
        val option = vote.options.firstOrNull{it.name == name}?:throw IllegalArgumentException("해당 이름의 옵션이 존재하지 않습니다.")
        return option.id!!
    }

    @Transactional
    fun uploadRouletteToVote(rouletteId: Long): VoteUploadResponse{
        val roulette = rouletteRepository.findById(rouletteId).orElseThrow { IllegalArgumentException("해당 룰렛이 존재하지 않습니다.") }
        val options = roulette.items.map{item -> VoteOption(name = item.name)}.toMutableList()
        val vote = Vote(
                title = roulette.title,
                createdByUserId = roulette.ownerId,
                options = options
        )
        val savedVote = voteRepository.save(vote)
        return VoteUploadResponse(
                voteId = savedVote.id!!,
                message = "룰렛이 투표로 업로드되었습니다."
        )
    }

}

