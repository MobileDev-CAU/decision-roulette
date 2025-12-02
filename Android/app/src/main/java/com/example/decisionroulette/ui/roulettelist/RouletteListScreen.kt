package com.example.decisionroulette.ui.roulettelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // ⬅️ 추가
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.TopicField
import TopicButton
import com.example.decisionroulette.ui.reusable.BackButton
import kotlinx.coroutines.flow.collectLatest
import com.example.decisionroulette.ui.reusable.VerticalScrollbarThumb
import com.example.decisionroulette.ui.theme.Galmuri


@Composable
fun TopicCreateScreen(
    onNavigateToCreateOption: (String) -> Unit,
    onNavigateToRoulette: (Int) -> Unit,
    onNavigateToBack:()->Unit,
    viewModel: TopicCreateViewModel = viewModel()
) {
    val state = viewModel.uiState
    val currentInputValue by viewModel.currentInput
    val openMenuId by viewModel.menuOpenTopicId
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TopicCreateUiEvent.NavigateToCreateOption -> {
                    onNavigateToCreateOption(event.topicTitle)
                }
                is TopicCreateUiEvent.NavigateToRoulette -> onNavigateToRoulette(event.rouletteId)
                TopicCreateUiEvent.NavigateToBack -> onNavigateToBack()
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(horizontal = 16.dp)
            .padding(horizontal = 40.dp) // 다른 뷰에서도 다 양옆 패딩 40으로 맞춰주기!!!!!!!!!
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

//        Spacer(modifier = Modifier.height(40.dp))
        BackButton(title = "My Roulette List", onClick = viewModel::onBackButtonClicked)
//        Spacer(modifier = Modifier.height(40.dp))

        Spacer(modifier = Modifier.weight(1f))

        Text(text = "What's your concern today?", fontSize = 20.sp)
        Text(
            text = "Choose a topic",
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 36.dp),
            fontSize = 15.sp
        )


        val listScrollState = rememberScrollState() // 스크롤 상태 정의

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)  // 보이는 최대 스크롤바 박스 높이
                .padding(bottom = 32.dp)
                .padding(horizontal = 10.dp)
        ) {
            // 1. Scrollable Column (주제 버튼 목록)
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(listScrollState),
//                    .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. 기존 주제 버튼 (rouletteId를 사용)
                    state.existingTopics.forEach { topic ->
                        val isSelected = state.selectedTopicId == topic.rouletteId
                        TopicButton(
                            title = topic.title,
                            isSelected = isSelected,
                            onClick = { viewModel.toggleTopicSelection(topic.rouletteId) },
                            isMenuExpanded = openMenuId == topic.rouletteId,
                            onMenuClick = { viewModel.onMoreOptionsSelected(topic.rouletteId) },
                            onDismissMenu = viewModel::dismissMenu,
                            onDelete = {
                                viewModel.deleteTopic(
                                    topic.rouletteId,
                                    isExisting = true
                                )
                            }
                        )
                    }

                    // 2. 사용자가 새로 생성한 주제 버튼 (tempId를 사용)
                    state.userCreatedTopics.forEach { userTopic -> // userCreatedOptions 대신 userCreatedTopics 사용
                        val isSelected = state.selectedTopicId == userTopic.tempId
                        TopicButton(
                            title = userTopic.title,
                            isSelected = isSelected,
                            onClick = { viewModel.toggleTopicSelection(userTopic.tempId) },
                            isMenuExpanded = openMenuId == userTopic.tempId,
                            onMenuClick = { viewModel.onMoreOptionsSelected(userTopic.tempId) },
                            onDismissMenu = viewModel::dismissMenu,
                            onDelete = {
                                viewModel.deleteTopic(
                                    userTopic.tempId,
                                    isExisting = false
                                )
                            }
                        )
                    }
                }
                // 2. Scroll Bar Thumb
                VerticalScrollbarThumb(
                    listScrollState = listScrollState,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        // ---------------------------------------------------------
        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
            // 새 주제 입력 필드
            TopicField(
                value = currentInputValue,
                onValueChange = viewModel::updateCurrentInput,
                label = "Enter a new topic.",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.addTopicFromInput()
                        focusManager.clearFocus()
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // choice 버튼
        Button(
            onClick = viewModel::onChoiceButtonClicked,
            enabled = state.selectedTopicId != null,
            modifier = Modifier
                .fillMaxWidth()
                .width(250.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF685C57)
            )
        )
        {
            Text("choice", color = Color.White, style = MaterialTheme.typography.titleMedium, fontFamily = Galmuri)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}