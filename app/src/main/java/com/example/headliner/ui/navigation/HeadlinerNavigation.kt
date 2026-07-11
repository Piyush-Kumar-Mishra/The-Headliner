package com.example.headliner.ui.navigation

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.headliner.ui.screens.dashboard.DashboardViewModel
import com.example.headliner.ui.screens.explore.ExploreViewModel
import com.example.headliner.domain.model.Article
import com.example.headliner.ui.screens.dashboard.DashboardScreen
import com.example.headliner.ui.screens.detail.ArticleDetailScreen
import com.example.headliner.ui.screens.explore.ExploreScreen
import com.example.headliner.ui.screens.notes.NotesScreen
import com.example.headliner.ui.screens.saved.SavedScreen
import com.example.headliner.ui.screens.search.SearchScreen
import com.example.headliner.ui.screens.settings.SettingsScreen
import com.example.headliner.ui.theme.NewspaperCharcoal
import com.example.headliner.ui.theme.NewspaperCream
import kotlinx.coroutines.delay

sealed class Screen(val route: String, val label: String) {
    data object Dashboard : Screen("dashboard", "Home")
    data object Search : Screen("search", "Search")
    data object Explore : Screen("explore", "Explore")
    data object Saved : Screen("saved", "Saved")
    data object Notes : Screen("notes", "Notes")
    data object Settings : Screen("settings", "Settings")
    data object ArticleDetail : Screen(
        route = "detail/{encodedUrl}?title={title}&imageUrl={imageUrl}&sourceName={sourceName}&publishedAt={publishedAt}&description={description}&content={content}&sourceUrl={sourceUrl}",
        label = "Article"
    ) {
        fun createRoute(article: Article): String =
            "detail/${Uri.encode(article.url)}?title=${Uri.encode(article.title)}" +
                    "&imageUrl=${Uri.encode(article.imageUrl ?: "")}" +
                    "&sourceName=${Uri.encode(article.sourceName)}" +
                    "&publishedAt=${Uri.encode(article.publishedAt)}" +
                    "&description=${Uri.encode(article.description ?: "")}" +
                    "&content=${Uri.encode(article.content ?: "")}" +
                    "&sourceUrl=${Uri.encode(article.sourceUrl ?: "")}"
    }
}

@Composable
fun HeadlinerApp() {
    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val exploreViewModel: ExploreViewModel = hiltViewModel()

    var showSplash by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }
    if (showSplash) {
        SplashScreen()
    }
    else {
        HeadlinerNavHost(dashboardViewModel, exploreViewModel)
    }
}

@Composable
private fun SplashScreen() {
    val alpha by animateFloatAsState(1f, label = "splashAlpha")
    Box(
        Modifier
            .fillMaxSize()
            .background(NewspaperCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.alpha(alpha)
        ) {
            Box(
                modifier = Modifier
                    .background(NewspaperCharcoal, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "THE",
                    color = NewspaperCream,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Headliner",
                color = NewspaperCharcoal,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                ),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(1.dp)
                    .background(NewspaperCharcoal.copy(alpha = 0.25f))
            )
            Text(
                text = "Your Daily News Companion",
                color = NewspaperCharcoal.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontFamily = FontFamily.Serif
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HeadlinerNavHost(
    dashboardViewModel: DashboardViewModel,
    exploreViewModel: ExploreViewModel
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomScreens = listOf(Screen.Dashboard, Screen.Explore, Screen.Saved, Screen.Notes, Screen.Settings)
    val showBottomBar = bottomScreens.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onSearch = { navController.navigate(Screen.Search.route) },
                        onOpenArticle = { navController.navigate(Screen.ArticleDetail.createRoute(it)) },
                        snackbarHostState = snackbarHostState,
                        viewModel = dashboardViewModel
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        onBack = { navController.popBackStack() },
                        onOpenArticle = { navController.navigate(Screen.ArticleDetail.createRoute(it)) },
                        snackbarHostState = snackbarHostState
                    )
                }
                composable(Screen.Explore.route) {
                    ExploreScreen(
                        onOpenArticle = { navController.navigate(Screen.ArticleDetail.createRoute(it)) },
                        snackbarHostState = snackbarHostState,
                        viewModel = exploreViewModel
                    )
                }
                composable(Screen.Saved.route) {
                    SavedScreen(onOpenArticle = { navController.navigate(Screen.ArticleDetail.createRoute(it)) })
                }
                composable(Screen.Notes.route) {
                    NotesScreen(onOpenArticle = { navController.navigate(Screen.ArticleDetail.createRoute(it)) })
                }
                composable(Screen.Settings.route) { SettingsScreen() }
                composable(
                    route = Screen.ArticleDetail.route,
                    arguments = listOf(
                        navArgument("encodedUrl") { type = NavType.StringType },
                        navArgument("title") { type = NavType.StringType; defaultValue = "" },
                        navArgument("imageUrl") { type = NavType.StringType; defaultValue = "" },
                        navArgument("sourceName") { type = NavType.StringType; defaultValue = "" },
                        navArgument("publishedAt") { type = NavType.StringType; defaultValue = "" },
                        navArgument("description") { type = NavType.StringType; defaultValue = "" },
                        navArgument("content") { type = NavType.StringType; defaultValue = "" },
                        navArgument("sourceUrl") { type = NavType.StringType; defaultValue = "" }
                    )
                ) {
                    ArticleDetailScreen(onBack = { navController.popBackStack() })
                }
            }

            if (showBottomBar) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                        .width(280.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = NewspaperCharcoal),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomScreens.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            IconButton(
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = if (isSelected) NewspaperCream.copy(alpha = 0.15f) else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = when (screen) {
                                        Screen.Dashboard -> Icons.Outlined.Home
                                        Screen.Explore -> Icons.Outlined.Explore
                                        Screen.Saved -> Icons.Outlined.BookmarkBorder
                                        Screen.Notes -> Icons.Outlined.EditNote
                                        Screen.Settings -> Icons.Outlined.Settings
                                        else -> Icons.Outlined.Home
                                    },
                                    contentDescription = screen.label,
                                    tint = if (isSelected) NewspaperCream else NewspaperCream.copy(alpha = 0.6f),
                                    modifier = Modifier.size(23.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
