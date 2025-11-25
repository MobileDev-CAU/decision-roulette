package com.example.decisionroulette.ui.topiclist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.TopicItem
import com.example.decisionroulette.ui.reusable.BlackBorder


// ⬅️ 상수 정의
private val MAX_CONTAINER_WIDTH = 400.dp

@Composable
fun TopicListScreen (
    onNavigateToCreateTopic: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopicListViewModel = viewModel()
){

     // 현재 토픽 리스트, 로딩 상태 등 화면 구성에 필요한 전체 UI 상태 스냅샷을 가져옴
    val state = viewModel.uiState

     // 현재 열려 있는 메뉴(토픽)의 ID를 반응형으로 가져옴 (값 변경 시 UI 자동 갱신)
    val menuOpenTopicId by viewModel.menuOpenTopicId


    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = "My Roulette List",
            modifier = Modifier.padding(top = 150.dp),
            fontSize = 36.sp
        )

        // -----------------------------------------------------------------


        Spacer(modifier = Modifier.weight(1f))

        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black),
            color = Color.White,
            modifier = Modifier
                .widthIn(max = MAX_CONTAINER_WIDTH)
                .fillMaxWidth(0.9f)
                .heightIn(max = 400.dp)
                .wrapContentHeight()
        ) {
            LazyColumn(
                modifier = Modifier.wrapContentHeight().fillMaxWidth()
            ) {
                items(items = state.topics, key = { it.rouletteId }) { topic ->
                    TopicItem(
                        topicTitle = topic.title,
                        onNavigateClick = { viewModel.onTopicSelected(topic) },
                        onMenuClick = { viewModel.onMoreOptionsSelected(topic) },
                        onDelete = { viewModel.onDeleteConfirmed(topic) },
                        isMenuExpanded = menuOpenTopicId == topic.rouletteId,
                        onDismissMenu = viewModel::dismissMenu
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        // -----------------------------------------------------------------

        BlackBorder(
            modifier = Modifier
                .width(350.dp)
                .padding(top = 24.dp, bottom = 40.dp),
            onClick = onNavigateToCreateTopic,
            text = "+"
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}