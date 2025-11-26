package com.mobApp.roulette.service

/*import com.mobApp.roulette.dto.Top3ItemResponse
import com.mobApp.roulette.dto.RouletteTop3Response
import com.mobApp.roulette.repository.FinalSelectionRepository
import jakarta.persistence.Id
import org.springframework.stereotype.Service

@Service
class RouletteAnalysisService(
        private  val finalSelectionRepository: FinalSelectionRepository
){
    fun getTop3FinalChoices(rouletteId: Long): RouletteTop3Response{
        val result = finalSelectionRepository.findTopChoices(rouletteId)
        val top3 = result.take(3).map{ row ->
            Top3ItemResponse(name = row["name"] as String,
                    count = (row["cnt"] as Long)
            )
        }
        return RouletteTop3Response(
                rouletteId = rouletteId,
                top3 = top3
        )
    }
}*/