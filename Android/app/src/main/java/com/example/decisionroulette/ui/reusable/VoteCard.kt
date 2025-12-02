package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.decisionroulette.api.vote.VoteListItem // [핵심 수정] 실제 API 모델 임포트
// import com.example.decisionroulette.ui.votelist.VoteItem // 이전 더미 모델 임포트가 있다면 제거
// import com.example.decisionroulette.ui.votelist.VoteOption // 이전 더미 모델 임포트가 있다면 제거

// VoteCard 컴포저블 수정
@Composable
fun VoteCard(
    // [핵심 수정] VoteItem 대신 VoteListItem 타입을 받도록 변경
    voteItem: VoteListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp) // 카드 높이
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White) // 배경색 설정
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween // 내용물 위아래 정렬
        ) {
            // 투표 제목 표시
            Text(
                text = voteItem.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            // 투표 항목 수 및 작성자 닉네임 표시 (VoteListItem 모델에 맞게 변경)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${voteItem.itemCount}개 항목", // itemCount 사용
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "작성자: ${voteItem.userNickname}", // userNickname 사용
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

// Preview를 업데이트하여 새로운 VoteListItem 모델을 사용하도록 변경
@Preview(showBackground = true)
@Composable
fun PreviewVoteCard() {
    VoteCard(
        voteItem = VoteListItem(
            voteId = 1L,
            title = "주말 데이트 장소",
            itemCount = 3,
            userNickname = "수인"
        ),
        onClick = {}
    )
}