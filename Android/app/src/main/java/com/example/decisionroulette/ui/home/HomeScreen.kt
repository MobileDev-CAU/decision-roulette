package com.example.decisionroulette.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.R
import com.example.decisionroulette.ui.reusable.BlackBorder
import androidx.compose.foundation.layout.width
import com.example.decisionroulette.ui.theme.Galmuri

@Composable
fun HomeScreen (
    onNavigateToTopicCreate: () -> Unit,
    modifier: Modifier = Modifier) {


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {


        Text(
            text = "Today's Roulette",
            modifier = Modifier
                .padding(top=120.dp),
            fontSize =36.sp
        )

        // -----------------------------------------------------------------


        Image(
            painter = painterResource(id = R.drawable.roulette),
            contentDescription = "룰렛 휠 이미지",
            modifier = Modifier
                .padding(vertical = 50.dp)
                .size(300.dp)
        )

        // -----------------------------------------------------------------


        BlackBorder(
            modifier = Modifier
                .width(250.dp),
//                .padding(top = 30.dp),

            onClick = onNavigateToTopicCreate,
            text = "START"
        )
    }
}

