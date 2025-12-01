package com.mobApp.roulette.service

import com.mobApp.roulette.domain.FinalSelection
import com.mobApp.roulette.dto.*
import com.mobApp.roulette.repository.FinalSelectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeedbackService(private  val finalSelectionRepository: FinalSelectionRepository) {
    @Transactional
    fun submitFeedback(req: FeedbackRequest, userId: Long?): FeedbackResponse {
        return FeedbackResponse("피드백 수신 완료")
    }

    @Transactional
    fun recordFinalChoice(req: FinalChoiceRequest, userId: Long?): FinalChoiceResponse {
        val sel = FinalSelection(
                userId = userId,
                rouletteId = req.rouletteId,
                rouletteResultItem = req.spinResult,
                finalChosenItem = req.finalChosenItem,
                wasFollowingResult = (req.spinResult == req.finalChosenItem),
        )
        finalSelectionRepository.save(sel)
        return FinalChoiceResponse("저장 완료")
    }
}