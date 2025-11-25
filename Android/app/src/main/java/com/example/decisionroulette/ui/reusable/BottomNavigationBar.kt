package com.example.decisionroulette.ui.reusable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List // 투표/리스트 아이콘으로 가정
import androidx.compose.material.icons.filled.Person // 마이페이지/프로필 아이콘으로 가정
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview // 프리뷰용
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState // ⬅️ 현재 라우트를 얻기 위해 필요
import androidx.compose.material.icons.filled.Poll // ⬅️ 투표 아이콘 (채워진)
import androidx.compose.material.icons.outlined.Poll // ⬅️ 투표 아이콘 (테두리)

// 각 내비게이션 아이템의 정보를 담는 데이터 클래스
data class BottomNavItem(
    val name: String, // 아이템 이름 (선택 사항)
    val route: String, // 라우트 경로 (필수)
    val selectedIcon: ImageVector, // 선택되었을 때 아이콘
    val unselectedIcon: ImageVector, // 선택되지 않았을 때 아이콘
    val hasBadge: Boolean = false, // 뱃지 표시 여부 (선택 사항)
    val badgeAmount: Int = 0 // 뱃지 숫자 (선택 사항)
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // 🚨 내비게이션 아이템 목록 정의 (Routes에 정의된 라우트 사용)
    val items = listOf(
        // TODO: Routes.HOME, Routes.VOTE_LIST, Routes.MY_PAGE 등 실제 라우트 사용
        BottomNavItem(
            name = "Home",
            route = "home_route", // ⬅️ 실제 라우트와 일치시켜야 합니다. (예: Routes.HOME)
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            name = "Vote",
            route = "vote_route", // ⬅️ 실제 라우트와 일치시켜야 합니다. (예: Routes.VOTE_LIST)
            selectedIcon = Icons.Filled.Poll, // ⬅️ Poll 아이콘 사용
            unselectedIcon = Icons.Outlined.Poll
        ),
        BottomNavItem(
            name = "MyPage",
            route = "login_route", // ⬅️ 실제 라우트와 일치시켜야 합니다. (예: Routes.MY_PAGE)
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    // 🚨 현재 백 스택 엔트리에서 현재 라우트 경로를 가져옵니다.
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White // 하단 바 배경색
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) { // 현재 라우트와 다를 때만 이동
                        navController.navigate(item.route) {
                            // 백 스택 관리 (선택 사항):
                            // 시작 화면으로 돌아가고, 다른 모든 목적지를 팝하여
                            // 뒤로 가기 버튼을 눌렀을 때 앱을 종료하도록 합니다.
                            // popUpTo(navController.graph.startDestinationId)
                            // launchSingleTop = true // 동일한 목적지 인스턴스가 여러 개 생성되지 않도록 합니다.
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.name,
                        tint = if (selected) Color.Black else Color.Gray // ⬅️ 선택 상태에 따라 색상 변경
                    )
                },
                // 텍스트를 표시하려면 label 파라미터를 추가합니다.
                label = {
                    Text(
                        text = item.name,
                        color = if (selected) Color.Black else Color.Gray // ⬅️ 텍스트 색상도 변경
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // 선택 시 나타나는 인디케이터 색상 (투명하게)
                )
            )
        }
    }
}
