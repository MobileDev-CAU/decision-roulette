package com.example.decisionroulette.ui.vote

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.home.VoteViewModel
import com.example.decisionroulette.ui.reusable.BackButton
import com.example.decisionroulette.ui.reusable.BlackBorder
import com.example.decisionroulette.ui.reusable.PieSlice
import com.example.decisionroulette.ui.reusable.VotePieChart


// 재사용할 필요가 없으므로 이 파일 내부에 정의합니다.
// ----------------------------------------------------

/**
 * 투표 결과 리스트 아이템에 필요한 데이터 구조 (MyVoteScreen 내부에서만 사용)
 */
data class VoteResultItem(
   val title: String,    // 항목 이름
   val percentage: Int,  // 투표 비율 (0~100)
   val voteCount: Int    // 득표 수
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
      Text(
         text = "${item.percentage}% (${item.voteCount}표)",
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

   Column(
      modifier = modifier
         .fillMaxSize()
         .padding(horizontal = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {

      // 뒤로 가기 버튼
      BackButton(title = "The result", onClick = onNavigateToBack)

      // ----------------- 차트 섹션 -----------------
      Spacer(modifier = Modifier.height(30.dp))
      Text(text = "주제", fontSize = 20.sp)


      Spacer(modifier = Modifier.weight(1f))


      val currentVoteData = listOf(
         PieSlice(Color.Red, 0.40f), // 40%
         PieSlice(Color.Blue, 0.35f), // 35%
         PieSlice(Color.Gray, 0.25f)  // 25%
      )

      VotePieChart(
         slices = currentVoteData,
         chartSize = 150.dp,
         modifier = Modifier.padding(20.dp)
      )
      // ----------------------------------------------

      Spacer(modifier = Modifier.height(30.dp))

      // ----------------- 리스트 섹션 -----------------
      // 투표 결과 데이터 예시 (서버에서 받아올 데이터)
      val results = listOf(
         VoteResultItem("파스타", 45, 9),
         VoteResultItem("치킨", 30, 6),
         VoteResultItem("국밥", 25, 5)
      )

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
      // -----------------------------------------------------------------


      BlackBorder(

         modifier = Modifier
            .width(250.dp)
            .padding(top = 40.dp),

         onClick = onNavigateToRoulette,
         text = "START"
      )

      Spacer(modifier = Modifier.weight(1f))


   }
}