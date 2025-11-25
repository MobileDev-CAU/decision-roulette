package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.R // 프로젝트의 R 파일 임포트
import com.example.decisionroulette.ui.votelist.VoteOption
import com.example.decisionroulette.ui.votelist.VoteItem
import com.example.decisionroulette.ui.theme.DecisionRouletteTheme // 앱 테마 임포트



@Composable
fun VoteCard(
    voteItem: VoteItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    val painter = remember {
        ColorPainter(Color.Red)
    }
    // 테두리와 배경을 가진 카드 형태
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium) // 카드 테두리
            .background(Color.White, MaterialTheme.shapes.medium) // 카드 배경색
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium) // 💡 클릭 시 ripple 효과를 위해 클립 추가
            .clickable(onClick = onClick)
    ) {
        // 사용자 아이콘과 제목
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            // 사용자 아이콘 (원형으로 클립)
            Image(
                painter = painter,
                contentDescription = "우선 임시로 색 넣어놓기 ----> 수정 : 사용자 id 연결해서 구분",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape) // 원형으로 자르기
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) // 아이콘 배경색 (예시)
                    .padding(4.dp)

            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = voteItem.title,
                style = MaterialTheme.typography.titleLarge, // 제목 스타일
                fontSize = 20.sp // 예시 폰트 크기
            )
        }

        Spacer(Modifier.height(16.dp))

        // 투표 내용 (원형 차트와 목록)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ⭐️ 왼쪽: 원형 차트 (간단한 더미. 실제 구현 시 더 복잡해질 수 있음)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), CircleShape) // 임시 원형 차트 배경
                    .border(1.dp, Color.Gray, CircleShape)
            ) {
                // 실제 차트 그리기 로직 (예: Canvas를 사용한 Arc 그리기)
                // 지금은 단순한 회색 원으로 대체합니다.
            }

            Spacer(Modifier.width(16.dp))

            // ⭐️ 오른쪽: 투표 옵션 리스트
            Column {
                voteItem.options.forEachIndexed { index, option ->
                    Text(
                        text = "${index + 1}위 ${option.name} ${option.percentage}%",
                        style = MaterialTheme.typography.bodyLarge, // 옵션 스타일
                        fontSize = 16.sp, // 예시 폰트 크기
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
