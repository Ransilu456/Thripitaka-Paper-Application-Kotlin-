package com.emarketing_thripitaka.application.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "මුල් පිටුව")
    object Downloads : BottomNavItem("downloads", Icons.Default.Download, "බාගැනීම්")
}
