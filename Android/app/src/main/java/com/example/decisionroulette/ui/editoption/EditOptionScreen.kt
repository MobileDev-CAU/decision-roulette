package com.example.decisionroulette.ui.editoption

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.BackButton
import com.example.decisionroulette.ui.reusable.BlackBorder
import com.example.decisionroulette.ui.reusable.OptionInputField
import com.example.decisionroulette.ui.reusable.VerticalScrollbarThumb
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp) // 패딩 통일
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 헤더: Edit Options
            BackButton(title = "Edit Options", onClick = viewModel::onBackButtonClicked)

            Spacer(modifier = Modifier.weight(1f))

            // 주제 제목 표시
            Text(text = "Current Topic", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = state.topicTitle,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 36.dp),
                fontSize = 17.sp
            )

            // 옵션 리스트 (스크롤 가능)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(listScrollState),
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
                            onRemove = null
                        )
                    }
                }
                VerticalScrollbarThumb(
                    listScrollState = listScrollState,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(vertical = 4.dp)
                )
            }

            // 하단 버튼 영역 (AI, + 버튼 삭제됨)
            Spacer(modifier = Modifier.height(24.dp))

            // Save 버튼 (Next -> Save)
            BlackBorder(
                onClick = viewModel::onSaveButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                text = "Save" // 문구 변경
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}