package com.example.decisionroulette.ui.votelist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // LaunchedEffect 임포트
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.R
import com.example.decisionroulette.ui.reusable.VoteCard
import com.example.decisionroulette.ui.votelist.VoteListViewModel
import com.example.decisionroulette.api.vote.VoteListItem
import com.example.decisionroulette.ui.votelist.VoteListUiEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

private val MAX_CONTAINER_WIDTH = 400.dp

@Composable
fun VoteListScreen (
    onNavigateToVoteStatus: (Long) -> Unit, // 상세 페이지 이동 시 voteId를 받도록 수정
    modifier: Modifier = Modifier,
    viewModel: VoteListViewModel = viewModel()
) {
    // ViewModel의 UI 상태를 수집
    val uiState by viewModel.uiState.collectAsState()

//    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
//        viewModel.loadVoteItems()
//    }

    // [핵심 추가] ViewModel의 이벤트를 수집하여 라우팅을 실행합니다.
    LaunchedEffect(key1 = Unit) {
        // ViewModel에서 발생하는 이벤트를 Flow로 수집
        viewModel.events.collect { event ->
            when (event) {
                is VoteListUiEvent.NavigateToVoteStatus -> {
                    // 이벤트에 포함된 voteId를 사용하여 상세 화면으로 이동합니다.
                    onNavigateToVoteStatus(event.voteId)
                }
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {

        Spacer(modifier = Modifier.height(70.dp))


        Text(
            text = "a real-time voting list",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 25.sp,
            textAlign = TextAlign.Center, // 텍스트 자체를 중앙 정렬

        )

        Spacer(modifier = Modifier.height(20.dp))

        // -----------------------------------------------------------------
        // 1. 로딩 상태 표시 (CircularProgressIndicator로 변경)
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator()
            }
            // 2. 에러 상태 표시
        } else if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "오류 발생",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
            // 3. 데이터 표시
        } else {
            // ViewModel의 실제 데이터 사용
            val voteItems = uiState.voteItems

            if (voteItems.isEmpty()) {
                Text(
                    text = "현재 진행 중인 투표가 없습니다.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            } else {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp), // 아이템 간 간격
                        contentPadding = PaddingValues(bottom = 16.dp) // 하단 패딩
                    ) {
                        // ViewModel에서 가져온 voteItems 사용
                        items(items = voteItems, key = { it.voteId }) { voteItem ->
                            VoteCard(
                                voteItem = voteItem, // 실제 데이터를 VoteCard에 전달
                                onClick = {
                                    // 클릭 이벤트를 ViewModel로 전달하여 이벤트 채널에 보냅니다.
                                    viewModel.onVoteItemClicked(voteItem.voteId)
                                }
                            ) // 재사용 가능한 VoteCard 컴포넌트 사용
                        }
                    }
                }
            }
        }
    }
}