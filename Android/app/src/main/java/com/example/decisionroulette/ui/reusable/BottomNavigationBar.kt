package com.example.decisionroulette.ui.reusable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Poll // 투표 아이콘 (채워진)
import androidx.compose.material.icons.filled.Person // 마이페이지/프로필 아이콘으로 가정
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Poll // 투표 아이콘 (테두리)
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState // ⬅️ 현재 라우트를 얻기 위해 필요
import com.example.decisionroulette.Routes // Routes 객체 Import 필수
import com.example.decisionroulette.ui.theme.Galmuri

// 각 내비게이션 아이템의 정보를 담는 데이터 클래스
data class BottomNavItem(
    val name: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasBadge: Boolean = false,
    val badgeAmount: Int = 0
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    onMyPageClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            name = "Home",
            route = Routes.HOME,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            name = "Vote",
            route = Routes.VOTE_LIST,
            selectedIcon = Icons.Filled.Poll,
            unselectedIcon = Icons.Outlined.Poll
        ),
        BottomNavItem(
            name = "MyPage",
            route = Routes.USER_PAGE,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White.copy(alpha = 0.0f)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (item.route == Routes.USER_PAGE) {
                        onMyPageClicked()
                    } else if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.name,
                        tint = if (selected) Color.Black else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.name,
                        color = if (selected) Color.Black else Color.Gray,
                        fontFamily = Galmuri
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}