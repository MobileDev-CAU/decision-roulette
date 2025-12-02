package com.example.decisionroulette.data.repository

import com.example.decisionroulette.api.RetrofitClient
import com.example.decisionroulette.api.vote.VoteApiService
import com.example.decisionroulette.api.vote.VoteListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException // HTTP 상태 코드를 포함하는 예외 처리용
import java.io.IOException
import java.lang.Exception
import android.util.Log // 로깅 추가
import com.example.decisionroulette.api.vote.VoteDetail
import com.example.decisionroulette.api.vote.VoteRouletteDetailResponse
import com.example.decisionroulette.api.vote.VoteSelectRequest
import com.example.decisionroulette.api.vote.VoteSelectResponse

// 참고: Kotlin 표준 라이브러리의 Result<T>는 data.common.Result 대신 사용됩니다.

class VoteRepository(
    // 기본값으로 RetrofitClient.voteInstance를 사용하여 DI 컨테이너 없이도 사용 가능하게 합니다.
    private val api: VoteApiService = RetrofitClient.voteInstance
) {
    private val TAG = "VoteRepository"

    /**
     * GET /vote/list: 전체 투표 목록을 조회합니다.
     * @return Result<List<VoteListItem>> Kotlin 표준 Result를 반환합니다.
     */
    suspend fun getVoteList(): Result<List<VoteListItem>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getVoteList()

            if (response.isSuccessful) {
                response.body()?.let {
                    // 성공 시: 데이터가 null이 아닐 경우 Result.success(T) 반환
                    Result.success(it)
                } ?: run {
                    // 성공했지만 본문이 null일 경우: 실패 처리
                    val errorMsg = "Server returned empty body on successful vote list fetch."
                    Log.e(TAG, errorMsg)
                    Result.failure(IOException(errorMsg))
                }
            } else {
                // HTTP 실패 코드 (4xx, 5xx) 처리
                val errorMsg = "HTTP Error: ${response.code()}"
                Log.e(TAG, "API call failed: $errorMsg, Body: ${response.errorBody()?.string()}")
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            // 네트워크 오류 (연결 끊김, Timeout 등)
            Log.e(TAG, "Network Error during getVoteList: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            // 그 외 예외 (JSON 파싱 오류 등)
            Log.e(TAG, "Unknown Error during getVoteList: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * GET /vote/{voteId}: 특정 투표의 상세 정보를 조회합니다.
     * @param voteId 조회할 투표의 ID
     * @return Result<VoteDetail> Kotlin 표준 Result를 반환합니다.
     */
    suspend fun getVoteDetail(voteId: Long): Result<VoteDetail> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getVoteDetail(voteId)

            if (response.isSuccessful) {
                response.body()?.let {
                    // 성공 시: 데이터가 null이 아닐 경우 Result.success(T) 반환
                    Result.success(it)
                } ?: run {
                    val errorMsg = "Server returned empty body on successful vote detail fetch."
                    Log.e(TAG, errorMsg)
                    Result.failure(IOException(errorMsg))
                }
            } else {
                // HTTP 실패 코드 (4xx, 5xx) 처리
                val errorMsg = "HTTP Error: ${response.code()}"
                Log.e(TAG, "API call failed: $errorMsg, Body: ${response.errorBody()?.string()}")
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            // 네트워크 오류
            Log.e(TAG, "Network Error during getVoteDetail: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            // 그 외 예외
            Log.e(TAG, "Unknown Error during getVoteDetail: ${e.message}")
            Result.failure(e)
        }
    }



    suspend fun getVoteRouletteDetail(voteId: Long): Result<VoteRouletteDetailResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getVoteRouletteDetail(voteId) // API 호출

            if (response.isSuccessful) {
                response.body()?.let {
                    // 성공 시: 데이터가 null이 아닐 경우 Result.success(T) 반환
                    Result.success(it)
                } ?: run {
                    // 성공했지만 본문이 null일 경우
                    val errorMsg = "Server returned empty body on successful vote roulette detail fetch."
                    Log.e(TAG, errorMsg)
                    Result.failure(IOException(errorMsg))
                }
            } else {
                // HTTP 실패 코드 (4xx, 5xx) 처리
                val errorMsg = "HTTP Error: ${response.code()}"
                Log.e(TAG, "API call failed: $errorMsg, Body: ${response.errorBody()?.string()}")
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            // 네트워크 오류 (연결 끊김, Timeout 등)
            Log.e(TAG, "Network Error during getVoteRouletteDetail: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            // 그 외 예외 (JSON 파싱 오류 등)
            Log.e(TAG, "Unknown Error during getVoteRouletteDetail: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun selectVote(voteId: Long, selectedOptionName: String, userId: Int): Result<VoteSelectResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. 요청 DTO 생성 (itemName만 포함)
            val requestBody = VoteSelectRequest(itemName = selectedOptionName)

            // 2. API 호출 (voteId와 userId는 URL로 전달)
            val response = api.selectVote(
                voteId = voteId,
                userId = userId,
                request = requestBody
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: run {
                    val errorMsg = "Server returned empty body on successful vote selection."
                    Log.e(TAG, errorMsg)
                    Result.failure(IOException(errorMsg))
                }
            } else {
                // HTTP 실패 코드 (4xx, 5xx) 처리
                val errorMsg = "HTTP Error during vote selection: ${response.code()}"
                Log.e(TAG, "API call failed: $errorMsg, Body: ${response.errorBody()?.string()}")
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            // 네트워크 오류
            Log.e(TAG, "Network Error during selectVote: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            // 그 외 예외
            Log.e(TAG, "Unknown Error during selectVote: ${e.message}")
            Result.failure(e)
        }
    }



}