package com.example.decisionroulette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember // ⭐ remember import
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decisionroulette.ui.home.HomeScreen
import com.example.decisionroulette.ui.home.HomeViewModel
import com.example.decisionroulette.ui.home.HomeUiEvent
import com.example.decisionroulette.ui.optioncreate.OptionCreateScreen
import com.example.decisionroulette.ui.optioncreate.OptionCreateUiEvent
import com.example.decisionroulette.ui.optioncreate.OptionCreateViewModel
import com.example.decisionroulette.ui.roulette.RouletteScreen
import com.example.decisionroulette.ui.theme.DecisionRouletteTheme
import com.example.decisionroulette.ui.roulettelist.TopicCreateScreen
import com.example.decisionroulette.ui.roulettelist.TopicCreateUiEvent
import com.example.decisionroulette.ui.roulettelist.TopicCreateViewModel
import com.example.decisionroulette.ui.auth.AuthViewModel
import com.example.decisionroulette.ui.auth.LoginScreen
import com.example.decisionroulette.ui.auth.SignUpScreen
import com.example.decisionroulette.ui.auth.AuthUiEvent
import com.example.decisionroulette.ui.reusable.BottomNavigationBar
import com.example.decisionroulette.ui.mypage.MyPageScreen
import com.example.decisionroulette.ui.votelist.VoteListScreen
import com.example.decisionroulette.ui.vote.MyVoteScreen
import com.example.decisionroulette.ui.votelist.VoteListUiEvent
import com.example.decisionroulette.ui.votelist.VoteListViewModel
import androidx.compose.foundation.Image
import com.example.decisionroulette.ui.editoption.EditOptionScreen
import com.example.decisionroulette.ui.auth.TokenManager
import com.example.decisionroulette.ui.home.VoteUiEvent
import com.example.decisionroulette.ui.home.VoteViewModel
import com.example.decisionroulette.ui.vote.OtherVoteScreen
import com.example.decisionroulette.data.repository.VoteRepository // Repository import
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.decisionroulette.api.auth.AuthRepository // AuthRepository import

// 화면 경로(Route)를 정의하는 상수 객체
object Routes {
    const val HOME = "home_route"
    //    const val TOPIC_LIST = "topic_list_route"
    const val TOPIC_CREATE="topic_create_route"
    const val OPTION_CREATE = "option_create_route"
    const val ROULETTE = "roulette_route"
    const val EDIT_OPTION = "edit_option_route"
    const val AI="ai_route"
    const val SIGN_UP = "sign_up_route"
    const val LOGIN = "login_route"
    const val USER_PAGE="user_page_route"
    const val VOTE_LIST="vote_list_route"
    // 🚨🚨 경로 수정: voteId를 파라미터로 받을 수 있게 경로를 변경
    const val VOTE_STATUS_MY = "vote_status_my_route/{voteId}"

    const val VOTE_STATUS_OTHER = "vote_status_other_route/{voteId}"


}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.initialize(this)

//        enableEdgeToEdge()
        setContent {
            DecisionRouletteTheme {
                AppScreen()
//                 Surface(
//                     modifier = Modifier.fillMaxSize(),
//                     color = MaterialTheme.colorScheme.background
//                 ) {
//                     RouletteScreen()
//                 }
            }
        }
    }
}

@Composable
fun AppScreen(
    homeViewModel: HomeViewModel = viewModel(),
//    topicListViewModel: TopicListViewModel = viewModel(),
    topicCreateViewModel: TopicCreateViewModel = viewModel(),
    optionCreateViewModel: OptionCreateViewModel=viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    //rouletteViewModel: RouletteViewModel =viewModel()
    voteListViewModel: VoteListViewModel=viewModel(),

    // ⭐ Repository를 AppScreen에서 직접 생성 (DI 컨테이너 대신 사용)
    // 크래시 방지를 위해 remember 블록 내에서 생성합니다.
    voteRepository: VoteRepository = remember { VoteRepository() },
    authRepository: AuthRepository = remember { AuthRepository() } // ⭐ AuthRepository도 remember로 생성
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val onMyPageClicked: () -> Unit = {
        if (authViewModel.uiState.isLoggedIn) {
            // 로그인 상태: 마이페이지(USER_PAGE)로 이동
            navController.navigate(Routes.USER_PAGE) {
                // 하단 탭 이동 최적화
                popUpTo(Routes.HOME) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            // 로그아웃 상태: 로그인 화면(LOGIN)으로 이동 (스택 전체 지움)
            navController.navigate(Routes.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    // 하단바 생성: 필요한 화면만 포함 (LOGIN, SIGN_UP은 제외)
    // TODO 투표리스트 포함해야함
    val BOTTOM_NAV_SCREENS = listOf(Routes.HOME, Routes.USER_PAGE, Routes.VOTE_LIST)


    // ------------------------------------------------------------------
    // 0. 인증 (로그인/회원가입) 네비게이션 처리
    LaunchedEffect(authViewModel.events) {
        authViewModel.events.collect { event ->
            when (event) {
                AuthUiEvent.NavigateToUserPage -> {
                    navController.navigate(Routes.USER_PAGE) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }

                AuthUiEvent.NavigateToSignUp -> {
                    // 🚨🚨 수정: 회원가입 화면 진입 전에 입력 필드 초기화 🚨🚨
                    authViewModel.clearAuthInputFields()
                    navController.navigate(Routes.SIGN_UP)
                }

                AuthUiEvent.NavigateToLogin -> {
                    // 🚨🚨 수정: 로그인 화면 진입 전에 입력 필드 초기화 🚨🚨
                    authViewModel.clearAuthInputFields()
                    navController.navigate(Routes.LOGIN)
                }

                else -> {}
            }
        }
    }
    // ------------------------------------------------------------------
    // 1. 홈 화면 -> 주제 목록 이동 (HomeViewModel 이벤트)
    LaunchedEffect(homeViewModel.events) {
        homeViewModel.events.collect { event ->
            when (event) {
                HomeUiEvent.NavigateToTopicCreate -> {
                    navController.navigate(Routes.TOPIC_CREATE)
                }

                else -> {}
            }
        }
    }

    // 2. 주제 목록 -> 주제 생성 화면 이동 (TopicListViewModel 이벤트)
//    LaunchedEffect(topicListViewModel.events) {
//        topicListViewModel.events.collect { event ->
//            when (event) {
//                TopicListUiEvent.NavigateToAddTopic -> {
//                    navController.navigate(Routes.TOPIC_CREATE)
//                }
//
//                else -> {}
//            }
//        }
//    }

    // 3. 주제 생성 (분기)
    LaunchedEffect(topicCreateViewModel.events) {
        topicCreateViewModel.events.collect { event ->
            when (event) {
                is TopicCreateUiEvent.NavigateToCreateOption -> {
                    navController.navigate("${Routes.OPTION_CREATE}/${event.topicTitle}")
                }

                is TopicCreateUiEvent.NavigateToRoulette -> {
                    navController.navigate("roulette_route/${event.rouletteId}")
                }

                TopicCreateUiEvent.NavigateToBack -> {
                    navController.navigate(Routes.TOPIC_CREATE)
                }
            }
        }
    }

    // 4. 옵션 생성 (분기)
    LaunchedEffect(optionCreateViewModel.events) {
        optionCreateViewModel.events.collect { event ->
            when (event) {
                is OptionCreateUiEvent.NavigateToRoulette -> {
                    navController.navigate("roulette_route/${event.rouletteId}")
                }

                OptionCreateUiEvent.NavigateAi -> {
                    navController.navigate(Routes.AI)
                }

                OptionCreateUiEvent.NavigateToBack -> {
                    navController.navigate(Routes.TOPIC_CREATE)
                }
            }
        }
    }
    // 🚨🚨 VoteListViewModel 이벤트 처리 수정 🚨🚨
    LaunchedEffect(voteListViewModel.events) {
        voteListViewModel.events.collect { event ->
            when (event) {
                // 🌟 NavigateToVoteStatus 이벤트에서 voteId와 isMyVote 플래그 추출
                is VoteListUiEvent.NavigateToVoteStatus -> {
                    val route = if (event.isMyVote) {
                        Routes.VOTE_STATUS_MY
                    } else {
                        Routes.VOTE_STATUS_OTHER
                    }
                    // 추출된 voteId를 경로에 포함하여 네비게이션
                    // 경로가 "vote_status_my_route/{voteId}" 형태이므로 {voteId} 부분을 대체합니다.
                    navController.navigate(route.replace("{voteId}", event.voteId.toString()))
                }

                else -> {}
            }
        }
    }

    // 주석 처리된 VoteViewModel 이벤트 처리 블록 제거 (각 화면 내에서 처리)

    if (BOTTOM_NAV_SCREENS.contains(currentRoute)) {
        Image(
            painter = painterResource(id = R.drawable.home_background6),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.basic_background2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        // 🚨 조건부 bottomBar 렌더링
        bottomBar = {
            if (BOTTOM_NAV_SCREENS.contains(currentRoute)) {
                BottomNavigationBar(navController = navController,onMyPageClicked = onMyPageClicked)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN, // 앱 시작 화면을 HOME으로 유지
            modifier = Modifier.padding(innerPadding)
        ) {

            // 🚨 1. 로그인 화면 (하단 바 없음)
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel, // ⬅️ AppScreen의 ViewModel 인스턴스 전달
                    onNavigateToUserPage = { navController.navigate(Routes.USER_PAGE) },
                    onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) }
                )
            }

            // 🚨 2. 회원가입 화면 (하단 바 없음)
            composable(Routes.SIGN_UP) {
                SignUpScreen(
                    viewModel = authViewModel, // ⬅️ AppScreen의 ViewModel 인스턴스 전달
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
                )
            }

            // 3. 홈 화면 (하단 바 있음)
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToTopicCreate = homeViewModel::onRouletteButtonClicked
                )
            }

            // 4. 주제 목록 (하단 바 있음)
//            composable(Routes.TOPIC_LIST) {
//                TopicListScreen(
//                    onNavigateToCreateTopic = topicListViewModel::onAddListButtonClicked,
//                    onNavigateBack = { navController.popBackStack() }
//                )
//            }

            // 🚨 5. 사용자 정보 화면 (MyPage) (하단 바 있음)
            composable(Routes.USER_PAGE) {
                MyPageScreen(
                    authViewModel = authViewModel, // ⬅️ AppScreen의 ViewModel 인스턴스 전달
                    navController = navController
                )
            }


            // 6. 주제 생성 (하단 바 없음)
            composable(Routes.TOPIC_CREATE) {
                TopicCreateScreen(
                    onNavigateToCreateOption = { title ->
                        navController.navigate("option_create_route/$title")
                    },
                    onNavigateToRoulette = { rouletteId ->
                        navController.navigate("roulette_route/$rouletteId")
                    },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            // 7. 옵션 생성 (하단 바 없음)
            composable(
                route = "option_create_route/{topicTitle}"
            ) { backStackEntry ->
                val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: "제목 없음"
                val viewModel: OptionCreateViewModel = viewModel()
                LaunchedEffect(topicTitle) {
                    viewModel.updateTitle(topicTitle)
                }
                OptionCreateScreen(
                    onNavigateToAi = { navController.navigate(Routes.AI) },
                    onNavigateToRoulette = { rouletteId ->
                        navController.navigate("roulette_route/$rouletteId")
                    },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            // 8. 룰렛 돌아가기
            composable("${Routes.ROULETTE}/{rouletteId}") { backStackEntry ->
                val rouletteId = backStackEntry.arguments?.getString("rouletteId")?.toIntOrNull() ?: -1

                RouletteScreen(
                    rouletteId = rouletteId,
                    onNavigateToVoteList = { navController.navigate(Routes.VOTE_LIST) },
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToEdit = {
                        navController.navigate("${Routes.EDIT_OPTION}/$rouletteId")
                    }
                )
            }

            composable("${Routes.EDIT_OPTION}/{rouletteId}") { backStackEntry ->
                val rouletteId = backStackEntry.arguments?.getString("rouletteId")?.toIntOrNull() ?: -1

                EditOptionScreen(
                    rouletteId = rouletteId,
                    onNavigateToRoulette = { id ->
                        navController.navigate("${Routes.ROULETTE}/$id") {
                            popUpTo("${Routes.ROULETTE}/$id") { inclusive = true }
                        }
                    },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            composable(Routes.VOTE_LIST) {
                VoteListScreen(
                    // onVoteItemClicked는 VoteListViewModel의 메서드를 참조하며, ViewModel이 이벤트를 발생시킵니다.
                    onNavigateToVoteStatus = voteListViewModel::onVoteItemClicked
                )
            }

            // 🚨🚨 VOTE_STATUS_MY 경로 처리 (파라미터 읽기)
            composable(
                route = Routes.VOTE_STATUS_MY,
                arguments = listOf(navArgument("voteId") { type = NavType.StringType }) // NavType 정의
            ) { backStackEntry ->
                // voteId는 ViewModel의 key로 사용하며, ViewModel은 SavedStateHandle로 argument를 읽습니다.
                val voteId = backStackEntry.arguments?.getString("voteId")

                // ⭐ Factory를 사용하여 ViewModel 생성 (크래시 방지)
                val voteViewModel: VoteViewModel = viewModel(
                    // ⭐ Key 안정화: backStackEntry ID 사용
                    key = backStackEntry.id,
                    factory = VoteViewModel.provideFactory(
                        voteRepository = voteRepository, // AppScreen에서 생성된 인스턴스
                        authRepository = authRepository // ⭐ AppScreen에서 생성된 AuthRepository 인스턴스
                    )
                )

                // ⭐ 개별 화면의 VoteViewModel 이벤트 처리
                LaunchedEffect(voteViewModel.events) {
                    voteViewModel.events.collect { event ->
                        when (event) {
                            VoteUiEvent.NavigateToBack -> { navController.popBackStack() }
                            VoteUiEvent.NavigateToRoulette -> { navController.navigate(Routes.ROULETTE) }
                            // 🚨 수정: VOTE_LIST로 명시적 이동
                            VoteUiEvent.NavigateToVoteClear -> { navController.navigate(Routes.VOTE_LIST) }
                        }
                    }
                }

                MyVoteScreen(
                    onNavigateToBack = { voteViewModel.onBackButtonClicked() }, // ViewModel 함수 호출
                    onNavigateToRoulette = { voteViewModel.onRouletteStartClicked() }, // 룰렛 시작 함수 호출
                    viewModel = voteViewModel // 인스턴스 전달
                )
            }

            // 🚨🚨 VOTE_STATUS_OTHER 경로 처리 (파라미터 읽기)
            composable(
                route = Routes.VOTE_STATUS_OTHER,
                arguments = listOf(navArgument("voteId") { type = NavType.StringType }) // NavType 정의
            ) { backStackEntry ->
                val voteId = backStackEntry.arguments?.getString("voteId")

                // ⭐ Factory를 사용하여 ViewModel 생성 (크래시 방지)
                val voteViewModel: VoteViewModel = viewModel(
                    // ⭐ Key 안정화: backStackEntry ID 사용
                    key = backStackEntry.id,
                    factory = VoteViewModel.provideFactory(
                        voteRepository = voteRepository,
                        authRepository = authRepository
                    )
                )

                LaunchedEffect(voteViewModel.events) {
                    voteViewModel.events.collect { event ->
                        when (event) {
                            VoteUiEvent.NavigateToBack -> { navController.popBackStack() }
                            VoteUiEvent.NavigateToRoulette -> { navController.navigate(Routes.ROULETTE) }
                            VoteUiEvent.NavigateToVoteClear -> { navController.navigate(Routes.VOTE_LIST) }
                        }
                    }
                }

                OtherVoteScreen(
                    viewModel = voteViewModel // 인스턴스 전달
                )
            }
        }
    }
}