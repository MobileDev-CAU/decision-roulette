package com.example.decisionroulette.ui.editoption

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.BackButton
import com.example.decisionroulette.ui.reusable.OptionInputField
import com.example.decisionroulette.ui.reusable.VerticalScrollbarThumb
import com.example.decisionroulette.ui.theme.Galmuri
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditOptionScreen(
    rouletteId: Int,
    onNavigateToRoulette: (Int) -> Unit,
    onNavigateToBack: () -> Unit,
    viewModel: EditOptionViewModel = viewModel()
) {
    val state = viewModel.uiState
    val scrollState = rememberScrollState()
    val listScrollState = rememberScrollState()

    // 1. 화면 진입 시 데이터 로드
    LaunchedEffect(rouletteId) {
        viewModel.loadRouletteData(rouletteId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                EditOptionUiEvent.NavigateToRoulette -> onNavigateToRoulette(rouletteId)
                EditOptionUiEvent.NavigateBack -> onNavigateToBack()
            }
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Black)
        }
    } else {
        // 전체 화면을 감싸는 Column
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 상단 헤더 (고정)
            // 좌우 패딩을 40.dp로 주어 다른 화면과 통일감을 줍니다.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                BackButton(title = "Edit Options", onClick = viewModel::onBackButtonClicked)
            }

            // 2. 스크롤 가능한 내용 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp) // 내용 패딩 통일
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 헤더와의 간격
                Spacer(modifier = Modifier.height(20.dp))

                // 주제 제목 표시
                Text(
                    text = "Today's Concern",
                    fontSize = 20.sp,
                    fontFamily = Galmuri,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "\" ${state.topicTitle.uppercase()} \"",
                    modifier = Modifier.padding(bottom = 36.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    fontFamily = Galmuri,
                    color = Color.Gray
                )

                // 옵션 리스트 (Box + Column)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(listScrollState)
                            .padding(end = 20.dp), // 스크롤바 공간 확보
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        state.options.forEachIndexed { index, option ->
                            OptionInputField(
                                index = index + 1,
                                value = option.value,
                                placeholder = "Enter a keyword",
                                onValueChange = { newValue ->
                                    viewModel.updateOptionValue(option.id, newValue)
                                },
                                onRemove = null // 수정 화면에서는 삭제 버튼 숨김 (필요시 추가 가능)
                            )
                        }
                    }
                    VerticalScrollbarThumb(
                        listScrollState = listScrollState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(vertical = 4.dp)
                    )
                }

                // 하단 버튼 영역
                Spacer(modifier = Modifier.height(48.dp))

                // Save 버튼 (갈색 테마 적용)
                Button(
                    onClick = viewModel::onSaveButtonClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF685C57) // 갈색 테마 적용
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Galmuri,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 하단 여백 확보
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}