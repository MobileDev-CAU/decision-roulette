package com.example.decisionroulette.ui.vote

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.home.VoteViewModel
import com.example.decisionroulette.ui.home.OptionItem // VoteViewModel이 사용하는 OptionItem import
import com.example.decisionroulette.ui.theme.Galmuri // ⭐ 폰트 임포트 (가정)
import androidx.compose.material3.CircularProgressIndicator
import com.example.decisionroulette.ui.reusable.BackButton // ⭐ BackButton import 추가
import com.example.decisionroulette.ui.votelist.MainBrown


@Composable
fun VoteOptionItem(
    item: OptionItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MainBrown else LightBrown
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val textColor = if (isSelected) MainBrown else Color.Black
    val voteColor = if (isSelected) MainBrown else Color.DarkGray


    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "option ${item.id + 1}",
                fontSize = 12.sp,
                fontFamily = Galmuri,
                color = LightBrown
            )
            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = item.title,
                fontSize = 18.sp,
                fontFamily = Galmuri,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${item.currentVotes}%",
                fontSize = 16.sp,
                fontFamily = Galmuri,
                fontWeight = FontWeight.ExtraBold,
                color = voteColor
            )
        }
    }
}


@Composable
fun OtherVoteScreen(
    modifier: Modifier = Modifier,
    viewModel: VoteViewModel = viewModel()
) {
    var selectedOptionId by remember { mutableStateOf<Int?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val optionsList = uiState.options

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BackButton("Vote for an Item", onClick = viewModel::onBackButtonClicked)

        // -------------------------------------------------------------------

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "\" ${uiState.title.uppercase()} \"",
            modifier = Modifier.padding(bottom =20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            fontFamily = Galmuri,
            color = Color.Gray
        )
        // -------------------------------------------------------------------

        Spacer(modifier = Modifier.height(80.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = MainBrown, modifier = Modifier.padding(top = 50.dp))
        } else if (uiState.errorMessage != null) {
            Text(
                uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                fontFamily = Galmuri
            )
        } else if (optionsList.isEmpty()) {
            Text(
                "There are no voting items.",
                color = LightBrown,
                fontFamily = Galmuri,
                modifier = Modifier.padding(top = 50.dp)
            )
        }


        // ----------------- 투표 항목 섹션 (스크롤 가능) -----------------
        Column(
            modifier = Modifier
                .heightIn(max = 350.dp)
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 10.dp),

            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            optionsList.forEach { item ->
                VoteOptionItem(
                    item = item,
                    isSelected = selectedOptionId == item.id,
                    onClick = {
                        selectedOptionId = if (selectedOptionId == item.id) null else item.id
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = selectedOptionId != null && !uiState.isLoading,
            onClick = {
                viewModel.vote(selectedOptionId)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MainBrown),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(
                text = "VOTE",
                fontSize = 18.sp,
                fontFamily = Galmuri,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(150.dp))
    }
}