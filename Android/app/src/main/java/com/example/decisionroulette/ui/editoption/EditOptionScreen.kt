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

    // 화면 진입 시 데이터 로드
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
            // 상단 헤더 (고정)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                BackButton(title = "Edit Options", onClick = viewModel::onBackButtonClicked)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(listScrollState)
                            .padding(end = 20.dp),
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
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = viewModel::onSaveButtonClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF685C57)
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

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}