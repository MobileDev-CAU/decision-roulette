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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.home.VoteViewModel
import com.example.decisionroulette.ui.home.OptionItem // VoteViewModel이 사용하는 OptionItem import

// 🚨🚨🚨 VoteViewModel과 충돌하는 OptionItem 정의 제거 🚨🚨🚨

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
            // item.id를 그대로 사용하기보다, 1부터 시작하는 순번으로 변환하여 표시하는 것이 좋습니다.
            Text(
                text = "option ${item.id + 1}",
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

            // ⭐ 투표율 표시 추가 (VoteViewModel의 OptionItem에는 currentVotes가 투표율(%)로 들어있음)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${item.currentVotes}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
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

    // 2. ViewModel로부터 UI 상태를 수집합니다.
    val uiState by viewModel.uiState.collectAsState()
    val optionsList = uiState.options // 투표 항목 목록

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

        // 🚨 수정: 상단 Spacer 제거 (Spacer(modifier = Modifier.weight(1f)))
        Spacer(modifier = Modifier.height(20.dp)) // 고정된 간격 추가

        // ⭐ 로딩 및 에러 상태 표시
        if (uiState.isLoading) {
            Text("투표 항목을 불러오는 중...", color = Color.Gray)
        } else if (uiState.errorMessage != null) {
            Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        } else if (optionsList.isEmpty()) {
            Text("투표 항목이 없습니다.", color = Color.Gray)
        }


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

        // 🚨 수정: 하단 Spacer에만 weight(1f)를 주어 남은 공간을 밀어내 Button을 하단에 붙입니다.
     //   Spacer(modifier = Modifier.weight(1f))


        Button(
            // 로딩 중이 아닐 때만 버튼 활성화
            enabled = selectedOptionId != null && !uiState.isLoading,
            onClick = {
                // 4. 투표하기 버튼 클릭 시 ViewModel의 vote 함수를 호출
                viewModel.vote(selectedOptionId)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                // ⭐ 수정: 하단 패딩 40dp 제거 (Scaffold 패딩을 따름)
                .padding(top = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("투표하기", color = Color.White)
        }
    }
}