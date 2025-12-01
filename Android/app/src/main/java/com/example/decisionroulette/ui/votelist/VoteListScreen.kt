package com.example.decisionroulette.ui.topiclist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.decisionroulette.ui.votelist.VoteOption
import com.example.decisionroulette.ui.votelist.VoteItem



private val MAX_CONTAINER_WIDTH = 400.dp

@Composable
fun VoteListScreen (
    onNavigateToVoteStatus: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VoteListViewModel = viewModel()
) {



    Column(
        modifier = modifier
            .fillMaxSize(),
            //.padding(horizontal = 10.dp),
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

        // -----------------------------------------------------------------

        val voteItems = listOf( // 이거 배경화면이랑 크기 맞춰야할듯..
            VoteItem(
                id = "1",
                title = "주말 데이트 장소",
                options = listOf(
                    VoteOption("서순라길", 70),
                    VoteOption("성수동", 20),
                    VoteOption("노들섬", 18)
                ),
            ),
            VoteItem(
                id = "2",
                title = "강남역 저녁 메뉴",
                options = listOf(
                    VoteOption("라멘", 70),
                    VoteOption("닭갈비", 20),
                    VoteOption("스시", 18)
                ),
            ),
            VoteItem(
                id = "3",
                title = "결혼식장 착장",
                options = listOf(
                    VoteOption("블랙 블라우스", 70),
                    VoteOption("화이트 셔츠", 20),
                    VoteOption("체크자켓", 18)
                ),
            ),
            VoteItem(
                id = "4",
                title = "저녁 메뉴",
                options = listOf(
                    VoteOption("마라엽떡", 70),
                    VoteOption("마라탕", 20),
                    VoteOption("치킨", 18)
                ),
            ),
            VoteItem(
                id = "5",
                title = "점심 메뉴",
                options = listOf(
                    VoteOption("김치찌개", 40),
                    VoteOption("돈까스", 30),
                    VoteOption("파스타", 30)
                ),
            )
        )

        Column(modifier = modifier.padding(horizontal = 16.dp)) {


            LazyColumn(

                verticalArrangement = Arrangement.spacedBy(8.dp), // 아이템 간 간격
                contentPadding = PaddingValues(bottom = 16.dp) // 하단 패딩
            ) {
                items(items = voteItems, key = { it.id }) { voteItem ->
                    VoteCard(
                        voteItem = voteItem,
                        onClick = {
                            onNavigateToVoteStatus() // 여기 일단 이렇게 해놓고 사용자 id 도 추가해서 자신/다른사람 분기 만들기
                        } )// 재사용 가능한 VoteCard 컴포넌트 사용
                }
            }

        }
    }
}



