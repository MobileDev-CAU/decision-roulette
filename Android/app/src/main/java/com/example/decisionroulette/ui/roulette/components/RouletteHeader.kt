package com.example.decisionroulette.ui.roulette.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.decisionroulette.ui.reusable.BackButton

@Composable
fun RouletteHeader(
    title: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        BackButton(title = title, onClick = onBackClick)
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 45.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}