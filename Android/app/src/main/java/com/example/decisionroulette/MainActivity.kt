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
import com.example.decisionroulette.ui.topiccreate.TopicCreateScreen
import com.example.decisionroulette.ui.topiccreate.TopicCreateUiEvent
import com.example.decisionroulette.ui.topiccreate.TopicCreateViewModel
//import com.example.decisionroulette.ui.topiclist.TopicListScreen
//import com.example.decisionroulette.ui.topiclist.TopicListUiEvent
//import com.example.decisionroulette.ui.topiclist.TopicListViewModel
import com.example.decisionroulette.ui.auth.AuthViewModel
import com.example.decisionroulette.ui.auth.LoginScreen
import com.example.decisionroulette.ui.auth.SignUpScreen
import com.example.decisionroulette.ui.auth.AuthUiEvent
import com.example.decisionroulette.ui.reusable.BottomNavigationBar
import com.example.decisionroulette.ui.mypage.MyPageScreen // ⬅️ MyPageScreen Import 추가 (가정)
import com.example.decisionroulette.ui.topiclist.VoteListScreen
import com.example.decisionroulette.ui.vote.MyVoteScreen
import com.example.decisionroulette.ui.votelist.VoteListUiEvent
import com.example.decisionroulette.ui.votelist.VoteListViewModel
import androidx.compose.foundation.Image


// 화면 경로(Route)를 정의하는 상수 객체
object Routes {
    const val HOME = "home_route"
//    const val TOPIC_LIST = "topic_list_route"
    const val TOPIC_CREATE="topic_create_route"
    const val OPTION_CREATE="option_create_route"
    const val ROULETTE="roulette_route"
    const val AI="ai_route"
    const val SIGN_UP = "sign_up_route"
    const val LOGIN = "login_route"
    const val USER_PAGE="user_page_route"
    const val VOTE_LIST="vote_list_route"
    const val VOTE_STATUS_MY = "vote_status_my_route"
    const val VOTE_STATUS_OTHER = "vote_status_other_route"




}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    voteListViewModel: VoteListViewModel=viewModel()

) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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

                AuthUiEvent.NavigateToSignUp -> navController.navigate(Routes.SIGN_UP)
                AuthUiEvent.NavigateToLogin -> navController.navigate(Routes.LOGIN)

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
                TopicCreateUiEvent.NavigateToCreateOption -> {
                    navController.navigate(Routes.OPTION_CREATE)
                }

                TopicCreateUiEvent.NavigateToRoulette -> {
                    navController.navigate(Routes.ROULETTE)
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
                OptionCreateUiEvent.NavigateToRoulette -> {
                    navController.navigate(Routes.ROULETTE)
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
    LaunchedEffect(voteListViewModel.events) {
        voteListViewModel.events.collect { event ->
            when (event) {
                VoteListUiEvent.NavigateToVoteStatus -> {
                    navController.navigate(Routes.VOTE_STATUS_MY)
                }

                else -> {}
            }
        }
    }

    // TODO 투표리스트 일때도 이 배경화면이도록
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
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME, // 앱 시작 화면을 HOME으로 유지
            modifier = Modifier.padding(innerPadding)
        ) {

            // 🚨 1. 로그인 화면 (하단 바 없음)
            composable(Routes.LOGIN) {
                LoginScreen(
                    // 로그인 성공/회원가입 이동은 ViewModel 이벤트로 처리됨
                    onNavigateToUserPage = { navController.navigate(Routes.USER_PAGE) },
                    onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) }
                )
            }

            // 🚨 2. 회원가입 화면 (하단 바 없음)
            composable(Routes.SIGN_UP) {
                SignUpScreen(
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
                    // 로그아웃 시 로그인 화면으로 이동
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onNavigateToEdit = { /* TODO: 수정 화면 경로 추가 */ }
                )
            }


            // 6. 주제 생성 (하단 바 없음)
            composable(Routes.TOPIC_CREATE) {
                TopicCreateScreen(
                    onNavigateToCreateOption = { navController.navigate(Routes.OPTION_CREATE) },
                    onNavigateToRoulette = { navController.navigate(Routes.ROULETTE) },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            // 7. 옵션 생성 (하단 바 없음)
            composable(Routes.OPTION_CREATE) {
                OptionCreateScreen(
                    onNavigateToAi = { navController.navigate(Routes.AI) },
                    onNavigateToRoulette = { navController.navigate(Routes.ROULETTE) },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }
            // 8. 룰렛 돌아가기
            composable(Routes.ROULETTE) {
                RouletteScreen(
                    onNavigateToVoteList = { navController.navigate(Routes.VOTE_LIST) },
                    onNavigateToBack = { navController.popBackStack() }
                )

            }

            composable(Routes.VOTE_LIST) {
                VoteListScreen(

                    onNavigateToVoteStatus = voteListViewModel::onVoteItemClicked


                )
            }

            composable(Routes.VOTE_STATUS_MY) {
                MyVoteScreen(

                )
            }
        }
    }
}
