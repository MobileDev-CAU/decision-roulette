package com.example.decisionroulette.api.roulette

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RouletteApiService {

    @GET("roulette/list")
    @Headers(
        "Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        "Connection: keep-alive",
        "Referer: http://15.165.9.218:8081/swagger-ui/index.html", // 서버가 이걸 체크하나 봅니다.
        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36",
        "accept: */*"
    )
    suspend fun getRouletteList(
        @Query("ownerId") ownerId: Int
    ): List<RouletteDto>
}