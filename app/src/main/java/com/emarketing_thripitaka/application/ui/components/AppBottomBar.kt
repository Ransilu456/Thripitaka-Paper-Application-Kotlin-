package com.emarketing_thripitaka.application.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.AppColors

@Composable
fun AppBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDark = isSystemInDarkTheme()

    NavigationBar(
        containerColor = if (isDark) AppColors.surface() else Color.White,
        tonalElevation = 0.dp,
    ) {
        BottomNavItem(
            label = "මුල් පිටුව",
            icon = Icons.Default.Home,
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { 
                            saveState = true 
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        BottomNavItem(
            label = "බාගැනීම්",
            icon = Icons.Default.Download,
            selected = currentRoute == Screen.Downloads.route,
            onClick = {
                if (currentRoute != Screen.Downloads.route) {
                    navController.navigate(Screen.Downloads.route) {
                        popUpTo(navController.graph.startDestinationId) { 
                            saveState = true 
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { 
            Icon(
                imageVector = icon, 
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            ) 
        },
        label = { 
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ) 
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppColors.primary(),
            selectedTextColor = AppColors.primary(),
            indicatorColor = AppColors.primary().copy(alpha = 0.12f),
            unselectedIconColor = AppColors.unselected(),
            unselectedTextColor = AppColors.unselected()
        )
    )
}
