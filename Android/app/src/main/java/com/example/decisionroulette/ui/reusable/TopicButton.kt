import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box // ⬅️ 추가: 정렬 컨테이너
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // ⬅️ 추가: Box 정렬 속성
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopicButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
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
        // ⬅️ 핵심 수정: Box 컨테이너를 사용하여 내부 콘텐츠(Text)를 중앙 정렬
        Box(
            modifier = Modifier.fillMaxSize(), // Box가 Surface 영역 전체를 차지하도록 설정
            contentAlignment = Alignment.Center // ⬅️ Box 내부의 항목을 수직/수평 중앙에 정렬
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                color = if (isSelected) Color.Black else Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center // ⬅️ 수평 정렬 (가로)
            )
        }
    }
}