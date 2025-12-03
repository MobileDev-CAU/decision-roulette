package com.example.decisionroulette.ui.votelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.reusable.VoteCard
import com.example.decisionroulette.ui.theme.Galmuri

val MainBrown = Color(0xFF685C57)
val LightBrown = Color(0xFFD7CCC8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteListScreen (
    onNavigateToVoteStatus: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VoteListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ViewModel 이벤트 처리 로직 (생략 없음)
    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is VoteListUiEvent.NavigateToVoteStatus -> {
                    onNavigateToVoteStatus(event.voteId)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Scaffold 패딩 적용
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Real-Time Voting List",
                fontSize = 25.sp,
                fontFamily = Galmuri,
                color = MainBrown,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------------------------------------------------

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator(color = MainBrown)
                }
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "Error loading voting list.",
                    modifier = Modifier.padding(24.dp),
                    fontFamily = Galmuri,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            } else {
                val voteItems = uiState.voteItems

                if (voteItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "There are no votes currently in progress.\n" +
                                    "Make a new vote!",
                            modifier = Modifier.padding(16.dp),
                            fontFamily = Galmuri,
                            textAlign = TextAlign.Center,
                            color = LightBrown,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    val reversedVoteItems = voteItems.reversed()

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(items = reversedVoteItems, key = { it.voteId }) { voteItem ->
                            VoteCard(
                                voteItem = voteItem,
                                onClick = {
                                    viewModel.onVoteItemClicked(voteItem.voteId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}