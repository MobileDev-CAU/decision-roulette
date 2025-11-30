import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box // ⬅️ 추가: 정렬 컨테이너
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // ⬅️ 추가: Box 정렬 속성
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decisionroulette.ui.theme.Galmuri

@Composable
fun TopicButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isMenuExpanded: Boolean,
    onMenuClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSelected) Color.Black else Color.Gray),
        color = if (isSelected) Color.Black.copy(alpha = 0.1f) else Color.White,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp), // 양옆 여백
            verticalAlignment = Alignment.CenterVertically // 수직 중앙 정렬
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                color = if (isSelected) Color.Black else Color.Gray,
                modifier = Modifier
                    .weight(1f),
                textAlign = TextAlign.Start
            )

            Box {
                Button(
                    onClick = onMenuClick, // 메뉴 열기
                    modifier = Modifier
                        .size(width = 40.dp, height = 40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent, // 배경 투명하게 (Surface 색 따르도록)
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(0.dp), // 패딩 제거해서 아이콘 중앙 정렬
                    elevation = null // 그림자 제거 (선택 사항)
                ) {
                    Text(
                        text = "⋮",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // 드롭다운 메뉴
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = onDismissMenu,
                    containerColor = Color.White
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Delete", color = Color.Red, fontFamily = Galmuri)
                            }
                        },
                        onClick = {
                            onDelete()
                            onDismissMenu()
                        }
                    )
                }
            }
        }
    }
}