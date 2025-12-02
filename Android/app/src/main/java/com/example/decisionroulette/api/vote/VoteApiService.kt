package com.example.decisionroulette.api.vote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VoteApiService {

    // ... 기존 함수들은 이미 올바른 suspend fun 형식입니다 ...

    /**
     * GET /vote/list
     * 전체 투표 목록을 조회합니다.
     * @return Response<List<VoteListItem>> 투표 목록 리스트
     */
    @GET("vote/list")
    suspend fun getVoteList(): Response<List<VoteListItem>>

    /**
     * GET /vote/{voteId}
     * 특정 투표의 상세 정보 (항목 및 투표율 포함)를 조회합니다.
     * @param voteId 조회할 투표의 ID
     * @return Response<VoteDetail> 투표 상세 정보
     */
    @GET("vote/{voteId}")
    suspend fun getVoteDetail(
        @Path("voteId") voteId: Long
    ): Response<VoteDetail>

    /**
     * POST /vote/upload
     * 룰렛 ID를 전송하여 투표를 업로드합니다.
     * @return Response<VoteUploadResponse> 업로드 결과 (voteId, message 포함)
     */
    @POST("/vote/upload")
    // -------------------------------------------------------------------
    // ⭐ 변경: 'fun'에 'suspend' 추가, 반환 타입을 'Call<T>'에서 'Response<T>'로 변경
    suspend fun uploadVote(
        @Body request: VoteUploadRequest
    ): Response<VoteUploadResponse>
    // -------------------------------------------------------------------


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