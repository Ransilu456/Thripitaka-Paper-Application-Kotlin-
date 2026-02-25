package com.emarketing_thripitaka.application.ui.navigation

import com.emarketing_thripitaka.application.R

sealed class DrawerNavItem(val route: String, val iconRes: Int, val label: String) {
    object Home : DrawerNavItem("home", R.drawable.ic_solar_home_smile, "මුල් පිටුව")
    object Services : DrawerNavItem("services", R.drawable.ic_solar_user_id, "අපගේ සේවාවන්")
    object About : DrawerNavItem("about", R.drawable.ic_solar_user_circle, "අප ගැන")
}
