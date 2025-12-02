package com.example.decisionroulette.ui.roulettelist

import TopicButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.BackButton
import com.example.decisionroulette.ui.reusable.TopicField
import com.example.decisionroulette.ui.reusable.VerticalScrollbarThumb
import com.example.decisionroulette.ui.theme.Galmuri
import kotlinx.coroutines.flow.collectLatest

// 🎨 디자인 컬러 (갈색)
private val CustomBrown = Color(0xFF685C57)

@Composable
fun TopicCreateScreen(
    onNavigateToCreateOption: (String) -> Unit,
    onNavigateToRoulette: (Int) -> Unit,
    onNavigateToBack: () -> Unit,
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

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CustomBrown)
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 헤더 (고정)
            Box(modifier = Modifier.padding(horizontal = 40.dp)) {
                BackButton(title = "My Roulette List", onClick = viewModel::onBackButtonClicked)
            }

            // 2. 스크롤 가능한 내용
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp) // 전체 패딩 40dp 통일
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f)) // 위쪽 여백 (적절히 조절됨)

                // 메인 텍스트
                Text(
                    text = "What's Your Concern Today?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Galmuri, // 폰트 적용
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Choose a topic",
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 36.dp),
                    fontSize = 15.sp,
                    fontFamily = Galmuri
                )

                // 3. 리스트 영역 (Box)
                val listScrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(horizontal = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(listScrollState),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 기존 주제 버튼
                        state.existingTopics.forEach { topic ->
                            val isSelected = state.selectedTopicId == topic.rouletteId
                            TopicButton(
                                title = topic.title,
                                isSelected = isSelected,
                                onClick = { viewModel.toggleTopicSelection(topic.rouletteId) },
                                isMenuExpanded = openMenuId == topic.rouletteId,
                                onMenuClick = { viewModel.onMoreOptionsSelected(topic.rouletteId) },
                                onDismissMenu = viewModel::dismissMenu,
                                onDelete = { viewModel.deleteTopic(topic.rouletteId, isExisting = true) }
                            )
                        }

                        // 사용자 생성 주제 버튼
                        state.userCreatedTopics.forEach { userTopic ->
                            val isSelected = state.selectedTopicId == userTopic.tempId
                            TopicButton(
                                title = userTopic.title,
                                isSelected = isSelected,
                                onClick = { viewModel.toggleTopicSelection(userTopic.tempId) },
                                isMenuExpanded = openMenuId == userTopic.tempId,
                                onMenuClick = { viewModel.onMoreOptionsSelected(userTopic.tempId) },
                                onDismissMenu = viewModel::dismissMenu,
                                onDelete = { viewModel.deleteTopic(userTopic.tempId, isExisting = false) }
                            )
                        }
                    }
                    // 스크롤바
                    VerticalScrollbarThumb(
                        listScrollState = listScrollState,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                // 4. 입력 필드
                Box(modifier = Modifier.padding(10.dp)) {
                    TopicField(
                        value = currentInputValue,
                        onValueChange = viewModel::updateCurrentInput,
                        label = "Enter A New Topic.",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.addTopicFromInput()
                                focusManager.clearFocus()
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // 5. Choice 버튼 (갈색 적용)
                Button(
                    onClick = viewModel::onChoiceButtonClicked,
                    enabled = state.selectedTopicId != null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomBrown,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp) // 둥근 모서리
                ) {
                    Text(
                        "Choice",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Galmuri
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 하단 여백 확보
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}