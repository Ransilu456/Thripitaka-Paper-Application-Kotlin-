package com.emarketing_thripitaka.application.ui.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emarketing_thripitaka.application.ui.components.AppBottomBar
import com.emarketing_thripitaka.application.ui.components.AppDrawer
import com.emarketing_thripitaka.application.ui.navigation.NavGraph
import com.emarketing_thripitaka.application.ui.navigation.Screen

@Composable
fun AppLayout() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine bottom bar visibility
    val showBottomBar = when (currentRoute) {
        Screen.Home.route, 
        Screen.Downloads.route, 
        Screen.PaperInfo.route -> true
        else -> false
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen, // Only enable gestures when open to avoid conflicts
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
                if (showBottomBar) {
                    AppBottomBar(navController)
                }
            }
        ) { innerPadding ->
            // We apply padding from Scaffold but also handle insets
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
