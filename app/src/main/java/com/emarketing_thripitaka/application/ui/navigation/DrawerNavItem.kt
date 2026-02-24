package com.emarketing_thripitaka.application.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DrawerNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : DrawerNavItem("home", Icons.Default.Home, "මුල් පිටුව")
    object Services : DrawerNavItem("services", Icons.Default.Star, "අපගේ සේවාවන්")
    object About : DrawerNavItem("about", Icons.Default.Info, "අප ගැන")
}
