package com.example.decisionroulette.ui.reusable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions // ⬅️ 필요
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun TopicField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    // ⬅️ 1. 오류 해결: keyboardOptions 파라미터를 추가했습니다.
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },

        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.LightGray.copy(alpha = 0.5f),
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Gray
        ),

        // ⬅️ 2. 외부에서 받은 keyboardOptions 객체를 사용합니다.
        keyboardOptions = keyboardOptions.copy(keyboardType = keyboardType),

        // ⬅️ 3. 외부에서 받은 keyboardActions 객체를 사용합니다.
        keyboardActions = keyboardActions,

        // Modifier를 통해 외부에서 크기 및 패딩 조정
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}