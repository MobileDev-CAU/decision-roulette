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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.api.vote.VoteListItem
import com.example.decisionroulette.ui.theme.Galmuri // Galmuri 폰트 임포트

// 🎨 디자인 컬러 (RouletteResultDialog에서 가져옴)
val MainBrown = Color(0xFF685C57)
val LightBrown = Color(0xFFD7CCC8)
val BackgroundWhite = Color(0xFFFDFBF7)

@Composable
fun VoteCard(
    voteItem: VoteListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp) // 높이를 최소값으로 설정하여 제목 길이에 유연하게 대응
            .clickable(onClick = onClick),

        // 둥근 모서리 및 입체감 강화
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),

        // 메인 브라운 색상의 얇은 테두리 적용
        border = BorderStroke(1.5.dp, LightBrown),

        // 배경색을 테마 화이트로 설정
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp), // 내부 패딩 증가
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 투표 제목 표시
            Text(
                text = voteItem.title,
                fontSize = 18.sp,
                fontFamily = Galmuri,
                fontWeight = FontWeight.Bold,
                color = MainBrown, // 제목에 메인 브라운 색상 적용
                maxLines = 2 // 긴 제목에 대비
            )

            Spacer(modifier = Modifier.height(8.dp)) // 제목과 정보 사이 간격

            // 투표 항목 수 및 작성자 닉네임 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom // Row의 요소들이 아래로 정렬되도록 함
            ) {
                // 항목 수 정보
                Text(
                    text = "${voteItem.itemCount} Items",
                    fontSize = 13.sp,
                    fontFamily = Galmuri,
                    fontWeight = FontWeight.SemiBold,
                    color = LightBrown // 보조 정보에 라이트 브라운 색상 적용
                )

                // 작성자 닉네임 정보
                Text(
                    text = "By ${voteItem.userNickname}",
                    fontSize = 13.sp,
                    fontFamily = Galmuri,
                    color = Color.Gray // 일반 회색 사용
                )
            }
        }
    }
}
