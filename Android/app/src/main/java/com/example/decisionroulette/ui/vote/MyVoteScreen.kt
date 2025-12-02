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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

// 재사용할 필요가 없으므로 이 파일 내부에 정의합니다.
// ----------------------------------------------------

/**
 * 투표 결과 리스트 아이템에 필요한 데이터 구조 (MyVoteScreen 내부에서만 사용)
 * VoteViewModel의 OptionItem 구조와 유사하나, 득표 수(voteCount)를 직접 포함
 */
data class VoteResultItem(
   val title: String,    // 항목 이름
   val percentage: Int,  // 투표 비율 (0~100)
   val voteCount: Int    // 득표 수 (⭐️ 이 데이터는 VoteDetail API에 없으므로 임시로 획득 불가. percentage만 사용)
)

/**
 * 투표 결과 리스트의 하나의 행을 그리는 컴포넌트 (MyVoteScreen 내부에서만 사용)
 */
@Composable
private fun VoteResultListItem(
   item: VoteResultItem,
   modifier: Modifier = Modifier
) {
   Row(
      modifier = modifier
         .fillMaxWidth()
         .padding(vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
   ) {
      // 1. 항목 이름 (좌측 정렬)
      Text(
         text = item.title,
         fontSize = 18.sp,
         modifier = Modifier.weight(0.4f)
      )

      // 2. 간격
      Spacer(modifier = Modifier.weight(0.1f))

      // 3. 비율 및 득표 수 (우측 정렬)
      // 득표 수(voteCount)는 API에서 제공되지 않으므로, 임시로 숨기거나 비율만 표시합니다.
      Text(
         text = "${item.percentage}%",
         fontSize = 18.sp,
         textAlign = TextAlign.End,
         modifier = Modifier.weight(0.5f)
      )
   }
}
// ----------------------------------------------------


@Composable
fun MyVoteScreen (
   modifier: Modifier = Modifier,
   onNavigateToBack: () -> Unit, // 이 파라미터는 현재 뷰모델의 버튼 클릭 로직으로 대체되었으나, 명세 유지
   onNavigateToRoulette: () -> Unit,
   viewModel: VoteViewModel = viewModel()
) {
   // ViewModel의 UI 상태를 수집합니다.
   val uiState by viewModel.uiState.collectAsState()

   // ViewModel의 OptionItem에는 'currentVotes'가 투표율(%)로 저장되어 있습니다.
   val optionsList = uiState.options

   // 투표 ID를 기반으로 화면 제목을 표시하기 위해 VoteDetail API를 다시 호출할 필요 없이,
   // VoteViewModel의 uiState에 VoteDetail의 title을 추가하여 사용하는 것이 더 효율적입니다.
   // 현재 VoteViewModel에는 title이 없으므로, 임시로 "투표 결과"를 사용합니다.
   val voteTitle = "투표 결과" // ⭐️ 실제로는 ViewModel에서 가져와야 함

   Column(
      modifier = modifier
         .fillMaxSize()
         .padding(horizontal = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {

      // 뒤로 가기 버튼
      BackButton(title = voteTitle, onClick = onNavigateToBack)

      // ----------------- 차트 섹션 -----------------
      Spacer(modifier = Modifier.height(30.dp))

      // ⭐ 로딩 및 에러 상태 표시
      if (uiState.isLoading) {
         Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("투표 결과를 불러오는 중...", color = Color.Gray)
         }
      } else if (uiState.errorMessage != null) {
         Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
      } else if (optionsList.isEmpty()) {
         Text("투표 항목이 없습니다.", color = Color.Gray)
      } else {
         // ------------------------------------------------
         // ⭐ 실제 데이터로 VotePieChart 데이터 생성
         // currentVotes는 투표율(%)이므로, 이를 기반으로 파이 차트 조각을 만듭니다.
         val colors = listOf(Color(0xFFD7CCC8),
            Color(0xFFFFCCBC),
            Color(0xFFC5E1A5),
            Color(0xFFFFF59D),
            Color(0xFFB39DDB),
            Color(0xFF80CBC4))
         val currentVoteData = optionsList.mapIndexed { index, item ->
            PieSlice(
               color = colors[index % colors.size], // 색상 순환 사용
               // 🚨 오류 수정: 'proportion' 대신 'ratio' 사용
               ratio = item.currentVotes.toFloat() / 100f // 투표율을 비율(0.0 ~ 1.0)로 변환
            )
         }

         VotePieChart(
            slices = currentVoteData,
            chartSize = 150.dp,
            modifier = Modifier.padding(20.dp)
         )
         // ----------------------------------------------

         Spacer(modifier = Modifier.height(30.dp))

         // ----------------- 리스트 섹션 -----------------
         // ⭐ 실제 데이터로 리스트 생성
         val results = optionsList.map { item ->
            VoteResultItem(
               title = item.title,
               percentage = item.currentVotes,
               voteCount = 0 // ⭐️ 득표 수는 VoteDetail API에 없으므로 0으로 임시 설정
            )
         }

         Column(
            modifier = Modifier
               .fillMaxWidth()
               .border(
                  width = 1.dp,
                  color = Color.Black,
                  shape = RoundedCornerShape(4.dp)
               )
               .padding(15.dp) // 리스트 내부 콘텐츠 패딩
         ) {
            // VoteResultListItem을 내부에서 호출
            results.forEach { item ->
               VoteResultListItem(item = item)
            }
         }

         Spacer(modifier = Modifier.height(40.dp)) // 하단 여백 추가

         BlackBorder(
            modifier = Modifier
               .width(250.dp)
               .padding(top = 40.dp),
            onClick = onNavigateToRoulette, // 룰렛 화면으로 이동 (START 버튼 역할)
            text = "START ROULETTE" // 텍스트 수정
         )
      }

      Spacer(modifier = Modifier.weight(1f))
   }
}

