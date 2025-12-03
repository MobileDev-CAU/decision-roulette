package com.example.decisionroulette.api.vote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VoteApiService {


   // 전체 투표 목록을 조회
    @GET("vote/list")
    suspend fun getVoteList(): Response<List<VoteListItem>>

   //특정 투표의 상세 정보 (항목 및 투표율 포함)를 조회
    @GET("vote/{voteId}")
    suspend fun getVoteDetail(
        @Path("voteId") voteId: Long
    ): Response<VoteDetail>

    //룰렛 ID를 전송하여 투표를 업로드
    @POST("/vote/upload")
    suspend fun uploadVote(
        @Query("userId") userId: Int,
        @Body request: VoteUploadRequest
    ): Response<VoteUploadResponse>


    @POST("vote/{voteId}/roulette")
    suspend fun getVoteRouletteDetail(
        @Path("voteId") voteId: Long
    ): Response<VoteRouletteDetailResponse>


    @POST("vote/{voteId}/vote")
    suspend fun selectVote(
        @Path("voteId") voteId: Long,
        @Query("userId") userId: Int,
        @Body request: VoteSelectRequest
    ): Response<VoteSelectResponse>
}