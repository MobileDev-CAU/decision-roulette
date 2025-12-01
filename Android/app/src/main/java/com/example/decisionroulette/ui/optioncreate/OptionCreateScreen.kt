package com.example.decisionroulette.ui.optioncreate

import com.example.decisionroulette.ui.reusable.BlackBorder
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.BackButton
import com.example.decisionroulette.ui.reusable.OptionInputField
import com.example.decisionroulette.ui.reusable.VerticalScrollbarThumb
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.sp


@Composable
fun OptionCreateScreen(
    onNavigateToRoulette: (Int) -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToBack: () -> Unit,
    viewModel: OptionCreateViewModel = viewModel()
) {
    val state = viewModel.uiState
    val screenScrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val listScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                OptionCreateUiEvent.NavigateAi -> onNavigateToAi()
                is OptionCreateUiEvent.NavigateToRoulette -> {
                    onNavigateToRoulette(event.rouletteId)
                }
                OptionCreateUiEvent.NavigateToBack -> onNavigateToBack()
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
                .padding(horizontal = 40.dp)
                .verticalScroll(screenScrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            Spacer(modifier = Modifier.height(40.dp))
            BackButton(title = "Fill the Roulette", onClick = viewModel::onBackButtonClicked)

//            Spacer(modifier = Modifier.height(40.dp))

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Today's Concern", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = state.topicTitle,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 36.dp),
                fontSize = 17.sp
            )


            // ---------------------------------------------------------


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(listScrollState),
//                        .padding(end = 20.dp),
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
                            onRemove = if (state.options.size > 1) {
                                { viewModel.removeOption(option.id) }
                            } else null
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

            // ---------------------------------------------------------
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1-1. '+' 버튼
                Button(
                    onClick = viewModel::addOption,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.5f),
                        contentColor = Color.Black
                    )
                ) {
                    Text("+", fontSize = 24.sp)
                }

                // 1-2. 'AI 추천' 버튼
                Button(
                    onClick = viewModel::onAiButtonClicked,
                    modifier = Modifier
                        .weight(1f) // ⬅️ 공간을 1/2로 나눔
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.5f),
                        contentColor = Color.Black
                    )
                ) {
                    Text("AI recommend", fontSize = 18.sp)
                }
            }

            // ---------------------------------------------------------

            BlackBorder(
                onClick = viewModel::onSaveButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                text = "Next"
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }

}