package com.example.decisionroulette.ui.optioncreate

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.BackButton
import com.example.decisionroulette.ui.reusable.OptionInputField
import com.example.decisionroulette.ui.reusable.VerticalScrollbarThumb
import com.example.decisionroulette.ui.optioncreate.components.AiRecommendationDialog
import com.example.decisionroulette.ui.theme.Galmuri
import kotlinx.coroutines.flow.collectLatest

private val CustomBrown = Color(0xFF685C57)
private val LightBrownBg = Color(0xFFEFEBE9)

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
                OptionCreateUiEvent.NavigateAi -> { }
                is OptionCreateUiEvent.NavigateToRoulette -> {
                    onNavigateToRoulette(event.rouletteId)
                }
                OptionCreateUiEvent.NavigateToBack -> onNavigateToBack()
            }
        }
    }

    if (state.showAiDialog) {
        AiRecommendationDialog(
            recommendations = state.aiRecommendations,
            onDismiss = { viewModel.dismissAiDialog() },
            onConfirm = { selectedItems ->
                viewModel.addAiSelectedOptions(selectedItems)
            }
        )
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CustomBrown)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp)
                .verticalScroll(screenScrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackButton(title = "Fill the Roulette", onClick = viewModel::onBackButtonClicked)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Today's Concern",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Galmuri,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = state.topicTitle,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 36.dp),
                fontSize = 17.sp,
                fontFamily = Galmuri
            )

            // 옵션 리스트 영역
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

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.addOption() },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBrownBg,
                        contentColor = CustomBrown
                    )
                ) {
                    Text("+", fontSize = 24.sp, fontFamily = Galmuri, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = viewModel::onAiButtonClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBrownBg,
                        contentColor = CustomBrown
                    )
                ) {
                    Text("AI recommend", fontSize = 16.sp, fontFamily = Galmuri, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = viewModel::onSaveButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomBrown
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Galmuri
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}