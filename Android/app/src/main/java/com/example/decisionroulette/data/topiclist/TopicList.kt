package com.example.decisionroulette.data.topiclist


// /roulette/list
data class RouletteList(

    val rouletteId: Int,
    val title: String,
    val itemCount: Int
)

data class RouletteListResponse(

    val rouletteLists: List<RouletteList>
)

// /roulette
data class RouletteListCreateRequest(

    val title: String,
    val items:List<String> ,
    val ownerId: Int

)

data class RouletteListCreateResponse(

    val rouletteId: Int,
    val title: String,
    val items:List<String> ,
    val ownerId: Int
)



// /roulette/ai/recommend

data class AiRecommentRequest(

    val title: String
)

data class AiRecommentResponse(

    val recommendations:List<String>
)



// /roulette/{Id}

data class RouletteDeleteResponse(

    val success: Boolean,
    val rouletteId: Int
)



