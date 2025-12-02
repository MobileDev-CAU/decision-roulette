package com.example.decisionroulette.ui.vote


import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.home.VoteViewModel
import com.example.decisionroulette.ui.home.OptionItem
import com.example.decisionroulette.ui.reusable.BlackBorder

// OptionItem.kt
data class OptionItem(
    val id: Int,
    val title: String // 예: "치킨", "피자"
)

@Composable
fun VoteOptionItem(
    item: OptionItem,
    isSelected: Boolean, // 현재 선택 상태
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 선택 상태에 따라 테두리 색상과 두께 변경
    val borderColor = if (isSelected) Color.Black else Color.Gray
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp) // 높이 고정
            .padding(vertical = 4.dp) // 항목 간 간격
            .clickable(onClick = onClick), // 클릭 이벤트 추가
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option 1, Option 2 등의 텍스트
            Text(
                text = "option ${item.id}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(10.dp))
            // 실제 항목 제목
            Text(
                text = item.title,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}


@Composable
fun OtherVoteScreen(
    modifier: Modifier = Modifier,
    onNavigateToVoteClear: () -> Unit,
    viewModel: VoteViewModel = viewModel()
) {
    // 1. 상태 관리: 선택된 옵션의 ID를 저장합니다.
    var selectedOptionId by remember { mutableStateOf<Int?>(null) }

    // 2. ViewModel로부터 투표 항목 데이터 StateFlow를 수집합니다.
    // Compose 상태로 변환하여 리스트를 그립니다.
    val optionsList by viewModel.options.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp), // 좌우 패딩
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(70.dp))


        Text(
            text = "Vote",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 25.sp,
            textAlign = TextAlign.Center, // 텍스트 자체를 중앙 정렬

        )

        Spacer(modifier = Modifier.weight(1f))


        // ----------------- 투표 항목 섹션 (스크롤 가능) -----------------
        Column(
            modifier = Modifier
                .heightIn(max = 350.dp)
                .verticalScroll(scrollState)
                .fillMaxWidth(),

            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 3. ViewModel에서 받은 optionsList 사용
            optionsList.forEach { item ->
                VoteOptionItem(
                    item = item,
                    isSelected = selectedOptionId == item.id,
                    onClick = {
                        // 옵션을 토글하여 선택 상태 업데이트
                        selectedOptionId = if (selectedOptionId == item.id) null else item.id
                    }
                )
            }
        }

        // 일단 뷰 보려는 용도
        BlackBorder(

            modifier = Modifier
                .width(250.dp)
                .padding(top = 40.dp),

            onClick = onNavigateToVoteClear,
            text = "VOTE"
        )

        // 이게 진자용
        Button(
            enabled = selectedOptionId != null,
            onClick = {
                // 4. 투표하기 버튼 클릭 시 ViewModel의 vote 함수를 호출
                viewModel.vote(selectedOptionId)

                // 5. 투표 완료 후 화면 전환 (선택적)
                // 만약 ViewModel에서 이벤트를 보내지 않는다면, 여기서 직접 호출합니다.
                // onNavigateToVoteClear()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(top = 20.dp, bottom = 40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("투표하기", color = Color.White)
        }


        Spacer(modifier = Modifier.weight(1f))

    }
}