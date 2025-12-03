package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OptionInputField(
    index: Int, // 순서 번호 (1부터 시작)
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    onRemove: (() -> Unit)? = null // 삭제 버튼이 필요한 경우에만 전달
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 'option N' 텍스트
        Text(
            text = "option $index",
            modifier = Modifier.width(75.dp)
        )

        Spacer(Modifier.width(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.LightGray
            )
        )

        if (onRemove != null) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove option")
            }
        } else {
            // 삭제 버튼이 없을 때 공간을 맞추기 위해
//            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}