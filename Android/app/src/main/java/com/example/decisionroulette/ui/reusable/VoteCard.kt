package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.ui.votelist.VoteItemUiModel
import com.example.decisionroulette.ui.theme.Galmuri

// 🎨 디자인 컬러 (RouletteResultDialog에서 가져옴)
val MainBrown = Color(0xFF685C57)
val LightBrown = Color(0xFFD7CCC8)
val BackgroundWhite = Color(0xFFFDFBF7)

val VoteIcon = Icons.Outlined.Poll

@Composable
fun VoteCard(
    voteItem: VoteItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.5.dp, LightBrown),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(25.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {
                // 투표 제목
                Text(
                    text = voteItem.title,
                    fontSize = 25.sp,
                    fontFamily = Galmuri,
                    fontWeight = FontWeight.Bold,
                    color = MainBrown,
                    maxLines = 2,
                )

                // -------------------------------------------------------------------

                // MY 배지 표시
                if (voteItem.isMyVote) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MY",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = Color(0xFFA1887F),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.Top)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                Text(
                    text = "${voteItem.itemCount} Items",
                    fontSize = 13.sp,
                    fontFamily = Galmuri,
                    fontWeight = FontWeight.SemiBold,
                    color = LightBrown
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        imageVector = VoteIcon,
                        contentDescription = "vote icon",
                        tint = MainBrown.copy(alpha = 0.7f),
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "By ${voteItem.userNickname}",
                        fontSize = 13.sp,
                        fontFamily = Galmuri,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}