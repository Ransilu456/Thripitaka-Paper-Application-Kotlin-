package com.emarketing_thripitaka.application.ui.layout

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emarketing_thripitaka.application.ui.components.AppBottomBar
import com.emarketing_thripitaka.application.ui.components.AppDrawer
import com.emarketing_thripitaka.application.ui.navigation.NavGraph
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.ThripitakaPaperApplicationTheme

@Composable
fun AppLayout() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar for all main screens so it remains stationary
    val showBottomBar = when (currentRoute) {
        Screen.Home.route, 
        Screen.Downloads.route, 
        Screen.Services.route,
        Screen.About.route -> true
        else -> false
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || showBottomBar,
        drawerContent = { 
            AppDrawer(
                navController = navController,
                drawerState = drawerState,
                scope = scope
            ) 
        }
    ) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    AppBottomBar(navController)
                }
            }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                drawerState = drawerState, 
                scope = scope
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppLayoutPreview() {
    ThripitakaPaperApplicationTheme {
        AppLayout()
    }
}
