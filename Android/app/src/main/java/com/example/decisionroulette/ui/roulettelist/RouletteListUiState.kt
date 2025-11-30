package com.example.decisionroulette.ui.roulettelist

import com.example.decisionroulette.data.topiclist.RouletteList


data class TopicCreateUiState(
    // 사용자가 입력 필드를 통해 직접 생성한 주제 목록 (tempId 사용)
    val userCreatedTopics: List<UserTopic> = emptyList(),

    // 현재 선택된 주제의 ID (기존 주제는 rouletteId, 사용자 생성 주제는 tempId)
    val selectedTopicId: Int? = null,

    // 서버 등에서 불러온 기존 주제 목록
    val existingTopics: List<RouletteList> = emptyList(),

    // 데이터 로딩 중인지 여부 (API 호출 시 유용)
    val isLoading: Boolean = false,

    // 발생한 에러 메시지 (선택 사항)
    val error: String? = null
)

data class UserTopic(
    val tempId: Int,
    val title: String
)
