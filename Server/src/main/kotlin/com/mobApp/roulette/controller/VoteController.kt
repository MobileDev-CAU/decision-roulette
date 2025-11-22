package com.mobApp.roulette.controller


import com.mobApp.roulette.dto.*
import com.mobApp.roulette.service.VoteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/vote")
class VoteController(
        private val voteService: VoteService
){
    @GetMapping("/list")
    fun getVoteList(): ResponseEntity<List<VoteListItemResponse>>{
        val votes = voteService.getAllVotes()
        return ResponseEntity.ok(votes)
    }
    @GetMapping("/{voteId}")
    fun getVoteDetail(
            @PathVariable voteId: Long
    ): ResponseEntity<VoteDetailResponse> {
        val detail = voteService.getVoteDetail(voteId)
        return ResponseEntity.ok(detail)
    }
    @PostMapping("/{voteId}/vote")
    fun castVote(
            @PathVariable voteId: Long,
            @RequestBody req: VoteRequest,
            @RequestParam userId: Long
    ): ResponseEntity<VoteResponse>{
        val optionId = voteService.findOptionIdByName(voteId, req.itemName)
        val result = voteService.castVote(voteId, optionId, userId)
        return ResponseEntity.ok(result)
    }
    @PostMapping("/{voteId}/roulette")
    fun voteToRoulette(
            @PathVariable voteId: Long
    ): ResponseEntity<VoteToRouletteResponse>{
        val result = voteService.voteToRoultte(voteId)
        return ResponseEntity.ok(result)
    }
    @PostMapping("/upload")
    fun uploadRouletteToVote(
            @RequestBody req: VoteUploadRequest
    ): ResponseEntity<VoteUploadResponse>{
        val result = voteService.uploadRouletteToVote(req.rouletteId)
        return ResponseEntity.ok(result)
    }
}