package com.mobApp.roulette.dto

data class CreateRouletteRequest(
        val title: String,
        val items: List<String>,
        val ownerId: Long
)

data class RouletteResponse(
        val rouletteId: Long,
        val ownerId: Long,
        val title: String,
        val items: List<RouletteItemResponse>
)
data class RouletteItemResponse(
        val itemId: Long,
        val name: String,
        val orderIndex: Int,
        val weight: Double
)
data class RouletteDetailResponse( ////필요한가?
        val rouletteId: Long,
        val title: String,
        val items: List<String>
)
data class RouletteSpinResponse(
        val result: String
)

data class UpdateRouletteRequest(
        val newItemName: String
)
data class UpdateRouletteResponse(
        val success: Boolean,
        val rouletteId: Long,
        val items: List<RouletteItemResponse>
)
data class RouletteListItemResponse(
        val rouletteId: Long,
        val title: String,
        val itemCount: Int
)
data class Top3ItemResponse(
        val name: String,
        val count: Long
)
data class RouletteTop3Response(
        val rouletteId: Long,
        val top3: List<Top3ItemResponse>
)

