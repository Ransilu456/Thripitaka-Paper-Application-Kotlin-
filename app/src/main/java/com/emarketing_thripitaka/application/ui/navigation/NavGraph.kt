package com.emarketing_thripitaka.application.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.emarketing_thripitaka.application.ui.screens.*
import com.emarketing_thripitaka.application.ui.screens.home.HomeScreen
import com.emarketing_thripitaka.application.ui.screens.downloads.DownloadsScreen
import com.emarketing_thripitaka.application.ui.screens.paperinfo.PaperInfoScreen
import com.emarketing_thripitaka.application.ui.screens.pdfviewer.PdfViewerScreen
import com.emarketing_thripitaka.application.ui.screens.welcome.WelcomeScreen
import kotlinx.coroutines.CoroutineScope
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object PaperInfo : Screen("paper_info/{exam}/{category}/{paperTitle}/{paperLink}?part={part}&year={year}") {
        fun createRoute(exam: String, category: String, paperTitle: String, paperLink: String, part: String?, year: String?) =
            "paper_info/${encode(exam)}/${encode(category)}/${encode(paperTitle)}/${encode(paperLink)}?part=${encode(part ?: "null")}&year=${encode(year ?: "null")}"
    }
    object Downloads : Screen("downloads")
    object PdfViewer : Screen("pdf_viewer/{paperLink}/{paperTitle}?year={year}&part={part}") {
        fun createRoute(paperLink: String, paperTitle: String, year: String?, part: String?) =
            "pdf_viewer/${encode(paperLink)}/${encode(paperTitle)}?year=${encode(year ?: "null")}&part=${encode(part ?: "null")}"
    }
    object About : Screen("about")
    object Services : Screen("services")

    protected fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    
    companion object {
        fun decode(value: String): String = URLDecoder.decode(value, StandardCharsets.UTF_8.toString())
    }
}

private val mainRoutes = setOf(
    Screen.Home.route,
    Screen.Downloads.route,
    Screen.Services.route,
    Screen.About.route
)

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    NavHost(
        navController = navController, 
        startDestination = Screen.Welcome.route,
        modifier = modifier,
        enterTransition = {
            val initial = initialState.destination.route
            val target = targetState.destination.route
            if (initial in mainRoutes && target in mainRoutes) {
                fadeIn(animationSpec = tween(220, easing = LinearOutSlowInEasing))
            } else {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350, easing = FastOutSlowInEasing)) + fadeIn(tween(350))
            }
        },
        exitTransition = {
            val initial = initialState.destination.route
            val target = targetState.destination.route
            if (initial in mainRoutes && target in mainRoutes) {
                fadeOut(animationSpec = tween(180))
            } else {
                slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(350, easing = FastOutSlowInEasing)) + fadeOut(tween(350))
            }
        },
        popEnterTransition = {
            val initial = initialState.destination.route
            val target = targetState.destination.route
            if (initial in mainRoutes && target in mainRoutes) {
                fadeIn(animationSpec = tween(220))
            } else {
                slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(350, easing = FastOutSlowInEasing)) + fadeIn(tween(350))
            }
        },
        popExitTransition = {
            val initial = initialState.destination.route
            val target = targetState.destination.route
            if (initial in mainRoutes && target in mainRoutes) {
                fadeOut(animationSpec = tween(180))
            } else {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(350, easing = FastOutSlowInEasing)) + fadeOut(tween(350))
            }
        }
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, drawerState, scope)
        }
        composable(
            route = Screen.PaperInfo.route,
            arguments = listOf(
                navArgument("exam") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
                navArgument("paperTitle") { type = NavType.StringType },
                navArgument("paperLink") { type = NavType.StringType },
                navArgument("part") { type = NavType.StringType; nullable = true },
                navArgument("year") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val exam = backStackEntry.arguments?.getString("exam") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val paperTitle = backStackEntry.arguments?.getString("paperTitle") ?: ""
            val paperLink = backStackEntry.arguments?.getString("paperLink") ?: ""
            val part = backStackEntry.arguments?.getString("part")
            val year = backStackEntry.arguments?.getString("year")
            
            PaperInfoScreen(navController, exam, category, paperTitle, paperLink, part, year)
        }
        composable(Screen.Downloads.route) {
            DownloadsScreen(navController, drawerState, scope)
        }
        composable(
            route = Screen.PdfViewer.route,
            arguments = listOf(
                navArgument("paperLink") { type = NavType.StringType },
                navArgument("paperTitle") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType; nullable = true },
                navArgument("part") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val paperLink = backStackEntry.arguments?.getString("paperLink") ?: ""
            val paperTitle = backStackEntry.arguments?.getString("paperTitle") ?: ""
            val year = backStackEntry.arguments?.getString("year")
            val part = backStackEntry.arguments?.getString("part")
            
            PdfViewerScreen(navController, paperLink, paperTitle, year, part)
        }
        composable(Screen.About.route) {
            AboutScreen(navController, drawerState)
        }
        composable(Screen.Services.route) {
            ServicesScreen(navController, drawerState, scope)
        }
    }
}
