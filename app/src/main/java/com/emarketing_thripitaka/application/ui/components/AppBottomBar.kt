package com.emarketing_thripitaka.application.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emarketing_thripitaka.application.ui.navigation.BottomNavItem
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.AppColors
import com.emarketing_thripitaka.application.ui.theme.ThripitakaPaperApplicationTheme

@Composable
fun AppBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDark = isSystemInDarkTheme()

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Downloads,
        BottomNavItem.Services,
        BottomNavItem.About
    )

    NavigationBar(
        containerColor = if (isDark) AppColors.surface() else Color.White,
        tonalElevation = 8.dp,
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            BottomNavItemView(
                label = item.label,
                iconRes = item.iconRes,
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { 
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
}

@Composable
private fun RowScope.BottomNavItemView(
    label: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { 
            Icon(
                painter = painterResource(id = iconRes),
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

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    ThripitakaPaperApplicationTheme {
        AppBottomBar(navController = rememberNavController())
    }
}
