package com.example.decisionroulette.data.votelist

// /vote/list
data class VoteLists(

    val rouletteId: Int,
    val title: String,
    val itemCount: Int


)

data class VoteListsResponse(

    val voteLists: List<VoteLists>
)

// /vote/{voteId}
data class VoteList(

    val name:String,

    val voteRate: Float
)


data class VoteListResponse(
    val voteId:Int,
    val title: String,
    val items:List<VoteList>
)

