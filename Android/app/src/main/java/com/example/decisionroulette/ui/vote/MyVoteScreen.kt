package com.example.decisionroulette.ui.vote


import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.home.VoteViewModel
import com.example.decisionroulette.ui.reusable.BackButton
import com.example.decisionroulette.ui.reusable.BlackBorder
import com.example.decisionroulette.ui.reusable.PieSlice
import com.example.decisionroulette.ui.reusable.VotePieChart
import com.example.decisionroulette.ui.home.VoteUiState // VoteUiState import
import com.example.decisionroulette.ui.theme.Galmuri // 폰트 임포트
import androidx.compose.ui.text.font.FontFamily // 폰트 패밀리 임포트
import androidx.compose.foundation.background
import com.example.decisionroulette.ui.votelist.LightBrown
import com.example.decisionroulette.ui.votelist.MainBrown

val MainBrown = Color(0xFF685C57)
val LightBrown = Color(0xFFD7CCC8)
val BackgroundWhite = Color(0xFFFDFBF7)



@Composable
private fun VoteChartLegend(
   data: List<Triple<String, Color, Int>>,
   modifier: Modifier = Modifier
) {
   Column(
      modifier = modifier
         .fillMaxWidth(0.9f)
         .padding(top = 16.dp, bottom = 24.dp)
         .border(1.dp, MainBrown, RoundedCornerShape(12.dp))
         .padding(12.dp)
   ) {
      data.forEach { (title, color, percentage) ->
         Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
         ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
               // 1. 색상 사각형
               Box(
                  modifier = Modifier
                     .size(12.dp)
                     .background(color, RoundedCornerShape(4.dp))
               )
               Spacer(modifier = Modifier.width(8.dp))
               Text(
                  text = title,
                  fontSize = 16.sp,
                  fontFamily = Galmuri,
                  color = MainBrown
               )
            }

            Text(
               text = "$percentage%",
               fontSize = 16.sp,
               fontFamily = Galmuri,
               fontWeight = FontWeight.Bold,
               color = MainBrown
            )
         }
      }
   }
}
// ----------------------------------------------------


@Composable
fun MyVoteScreen (
   modifier: Modifier = Modifier,
   onNavigateToBack: () -> Unit,
   onNavigateToRoulette: () -> Unit,
   viewModel: VoteViewModel = viewModel()
) {
   val uiState by viewModel.uiState.collectAsState()

   val optionsList = uiState.options

   val voteTitle = uiState.title.ifEmpty { "the results of the vote" }

   Column(
      modifier = modifier
         .fillMaxSize()
         .verticalScroll(rememberScrollState())
         .padding(horizontal = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      BackButton("Voting status", onClick = onNavigateToBack)

      // -------------------------------------------------------------------

      Spacer(modifier = Modifier.height(40.dp))


      Text(
         text = "\" ${voteTitle.uppercase()} \"",
         modifier = Modifier.padding(bottom =20.dp),
         fontWeight = FontWeight.Bold,
         fontSize = 25.sp,
         fontFamily = Galmuri,
         color = Color.Gray
      )

      // -------------------------------------------------------------------

      if (uiState.isLoading) {
         Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MainBrown)
         }
      } else if (uiState.errorMessage != null) {
         Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, fontFamily = Galmuri)
      } else if (optionsList.isEmpty()) {
         Text("There are no voting items.", color = LightBrown, fontFamily = Galmuri)
      } else {
         // ------------------------------------------------
         val colors = remember {
            listOf(
               Color(0xFFE0B99B), // 연한 베이지
               Color(0xFFB39D85), // 중간 갈색
               Color(0xFFD7CCC8), // 라이트 브라운
               Color(0xFFA1887F), // 로즈 골드
               Color(0xFF795548), // 짙은 갈색
               Color(0xFF6D4C41)  // 다크 브라운
            )
         }

         val currentVoteData = optionsList.mapIndexed { index, item ->
            PieSlice(
               color = colors[index % colors.size],
               ratio = item.currentVotes.toFloat() / 100f
            )
         }

         VotePieChart(
            slices = currentVoteData,
            chartSize = 150.dp,
            modifier = Modifier.padding(20.dp)
         )
         // ----------------------------------------------

         val legendData = optionsList.mapIndexed { index, item ->
            Triple(item.title, colors[index % colors.size], item.currentVotes)
         }

         VoteChartLegend(data = legendData)

         // -------------------------------------------------------------------

         Spacer(modifier = Modifier.height(40.dp))

         Button(
            onClick = { viewModel.onRouletteStartClicked() },
            colors = ButtonDefaults.buttonColors(containerColor = MainBrown),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            modifier = Modifier.fillMaxWidth(0.65f)
         ) {
            Text(
               text = "START ROULETTE",
               fontSize = 16.sp,
               fontFamily = Galmuri,
               fontWeight = FontWeight.Bold,
               color = Color.White
            )
         }
      }

      Spacer(modifier = Modifier.weight(1f))
   }
}