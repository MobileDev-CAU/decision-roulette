package com.mobApp.roulette.service

import com.mobApp.roulette.dto.AIRecommendRequest
import com.mobApp.roulette.dto.AIRecommendResponse
import org.springframework.stereotype.Service
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@Service
class AIService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // 보안상 API 키는 나중에 꼭 가려야함
    private val apiKey = "AIzaSyBvfdtJp5UdNsW4nRtk7eZF9eY1zfu5aw8"

    fun recommend(req: AIRecommendRequest): AIRecommendResponse {

        val title = req.title
        val history = req.history ?: emptyList() // 데이터 없으면 빈 리스트 처리
        val popular = req.popular ?: emptyList()

        val prompt = """
            Topic: $title
            User’s past selections: ${history.joinToString(", ")}
            Most voted by friends: ${popular.joinToString(", ")}
            
            Recommend 5 specific keywords in English that match the topic and have relevance to the user’s past selections.
            Among the 5 keywords, you must include items from both the user’s past selections and the ones most voted by friends.
            Do NOT use abstract categories—only concrete keywords are allowed.
            Provide only the keywords, separated by commas, with no explanations. (Example: pizza, hamburger, sushi)
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }.toString()

        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

        // 주소 (Gemini 2.0 Flash)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            if (response.isSuccessful && responseData != null) {
                val jsonResponse = JSONObject(responseData)
                val resultText = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                    .trim()

                return AIRecommendResponse(resultText)

            } else {
                return AIRecommendResponse("AI 오류: ${response.code}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return AIRecommendResponse("서버 통신 실패")
        }
    }
}