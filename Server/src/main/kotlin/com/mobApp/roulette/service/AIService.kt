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
import org.springframework.beans.factory.annotation.Value
import com.mobApp.roulette.dto.AIAnalyzeRequest
import com.mobApp.roulette.dto.AIAnalysisResponse
import com.mobApp.roulette.dto.AnalysisItem

@Service
class AIService(
    // 설정 파일(application.properties)에서 값을 가져옵니다.
    @Value("\${gemini.api.key}")
    private val apiKey: String
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun recommend(req: AIRecommendRequest): AIRecommendResponse {

        val title = req.title
        val history = req.history ?: emptyList() // 데이터 없으면 빈 리스트 처리
        val popular = req.popular ?: emptyList()

        // ✅ 데이터 유무에 따라 프롬프트 조정
        val historyText = if (history.isNotEmpty()) {
            "User's past selections: ${history.joinToString(", ")}"
        } else {
            "User's past selections: None (first-time user or new topic)"
        }

        val popularText = if (popular.isNotEmpty()) {
            "Items with the most votes from other users: ${popular.joinToString(", ")}"
        } else {
            "Items with the most votes from other users: None (new topic)"
        }

        val includeRule = when {
            history.isNotEmpty() && popular.isNotEmpty() ->
                """2. You MUST include at least 1 item from "User's past selections" AND at least 1 item from "Items with the most votes from other users" in your recommendation."""

            history.isNotEmpty() ->
                """2. You MUST include at least 1 item from "User's past selections" in your recommendation."""

            popular.isNotEmpty() ->
                """2. You MUST include at least 1 item from "Items with the most votes from other users" in your recommendation."""

            else ->
                """2. Since no historical data is available, recommend 5 items based purely on the topic."""
        }

        val prompt = """
            Topic requested by user: $title
            $historyText
            $popularText

            You are a keyword recommendation assistant. You must adhere to the following rules:
            1. Recommend exactly 5 concrete keywords in English based on the provided topic and data.
            $includeRule
            3. Abstract categories are strictly forbidden; provide specific items only.
            4. Output format: Provide ONLY the words separated by commas, without any explanation or numbering. (e.g., Pizza, Hamburger, Sushi)
            
            Recommend 5 keywords based on the data above.
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
                    // AI가 가끔 줄바꿈을 포함할 수 있어서 줄바꿈도 제거
                    .replace("\n", "")
                    .trim()

                // 쉼표(,)로 잘라서 리스트로 변환
                val keywordList = resultText.split(",")
                    .map { it.trim() } // 잘린 단어 앞뒤 공백 제거 (" 피자" -> "피자")
                    .filter { it.isNotEmpty() } // 혹시 모를 빈 문자열 제거

                // 변경된 DTO에 리스트를 담아서 반환
                return AIRecommendResponse(keywordList)

            } else {
                // 에러 상황에서도 빈 리스트나 에러 메시지를 리스트에 담아 보내야 함
                return AIRecommendResponse(listOf("AI 오류: ${response.code}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return AIRecommendResponse(listOf("서버 통신 실패"))
        }
    }

    // 리스트를 인자로 받습니다 (예: ["피자", "햄버거"])
    fun analyzeItems(req: AIAnalyzeRequest): AIAnalysisResponse {

        // 1. 프롬프트 작성
        val prompt = """
            Topic: ${req.title}
            Compare and analyze the following list of items: ${req.items.joinToString(", ")}

            For each item, clearly write 1 'pro' and 1 'con' in English.

            [IMPORTANT] You must output the result ONLY in the **JSON Array format** below. Do not add any other text or explanations.

            Format Example:
            [
              { "item": "Item Name", "pros": "Content of pros", "cons": "Content of cons" },
              { "item": "Item Name 2", "pros": "Content of pros", "cons": "Content of cons" }
            ]
        """.trimIndent()

        // 2. AI에게 요청 보내기
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
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

        val request = Request.Builder().url(url).post(requestBody).build()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            if (response.isSuccessful && responseData != null) {
                // 3. AI 응답에서 텍스트 추출
                val jsonResponse = JSONObject(responseData)
                var resultText = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                // (혹시 모를 마크다운 코드 블록 ```json ... ``` 제거)
                resultText = resultText.replace("```json", "").replace("```", "").trim()

                // 4. 문자열(JSON String)을 실제 객체 리스트로 변환
                val jsonArray = JSONArray(resultText) // 문자열을 배열로 변환
                val analysisList = ArrayList<AnalysisItem>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val item = AnalysisItem(
                        item = obj.getString("item"),
                        pros = obj.getString("pros"),
                        cons = obj.getString("cons")
                    )
                    analysisList.add(item)
                }

                return AIAnalysisResponse(analysisList)

            } else {
                // 에러 처리: 빈 리스트 반환하거나 에러용 더미 데이터 반환
                return AIAnalysisResponse(emptyList())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return AIAnalysisResponse(emptyList())
        }
    }
}