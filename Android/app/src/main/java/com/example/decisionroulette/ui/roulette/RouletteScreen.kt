package com.example.decisionroulette.ui.roulette

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.roulette.components.*

@Composable
fun RouletteScreen(
    rouletteId: Int,
    voteId: Long = -1L,
    viewModel: RouletteViewModel = viewModel(),
    onNavigateToVoteList: () -> Unit,
    onNavigateToBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rotation = remember { Animatable(0f) }
    val scrollState = rememberScrollState()

    LaunchedEffect(rouletteId, voteId) {
        if (voteId > 0) {
            viewModel.loadRouletteFromVote(voteId)
        } else if (rouletteId > 0) {
            viewModel.loadRouletteDetail(rouletteId)
        }
    }

    LaunchedEffect(uiState.isSpinning) {
        if (uiState.isSpinning) {
            val finalTarget = rotation.value + uiState.targetRotation
            rotation.animateTo(
                targetValue = finalTarget,
                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
            )
            viewModel.onSpinFinished()
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CustomBrown)
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 헤더 (고정)
            Box(modifier = Modifier.padding(horizontal = 40.dp)) {
                RouletteHeader(
                    title = uiState.title,
                    onBackClick = onNavigateToBack,
                    onEditClick = onNavigateToEdit
                )
            }

            // 2. 스크롤 가능한 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // 모드 스위치
                ModeToggleSwitch(
                    isVoteMode = uiState.isVoteMode,
                    onToggle = { isVote -> viewModel.toggleMode(isVote) }
                )

                Spacer(modifier = Modifier.height(30.dp))

                // AI 분석 리포트
                AiAnalysisExpander(analysisResult = uiState.analysisResult)

                Spacer(modifier = Modifier.height(80.dp)) // 룰렛과 간격 확보

                // 룰렛 휠
                RouletteWheel(
                    items = uiState.items,
                    rotationValue = rotation.value,
                    onStartClick = { viewModel.startSpin(rotation.value) }
                )

                // 하단 여백 (스크롤 시 잘림 방지)
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // 결과 팝업
    if (uiState.showResultDialog && uiState.spinResult != null) {
        RouletteResultDialog(
            resultName = uiState.spinResult!!,
            onDismiss = { viewModel.closeDialog() },
            onRetry = {
                viewModel.retrySpin()
                viewModel.startSpin(rotation.value)
            },
            onVote = {
                viewModel.uploadVote()
                onNavigateToVoteList()
            },
            onFinalConfirm = { finalChoice, satisfied ->
                viewModel.saveFinalChoice(finalChoice, satisfied)
            }
        )
    }
}