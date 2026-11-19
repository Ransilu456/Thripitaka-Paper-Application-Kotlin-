package com.emarketing_thripitaka.application.ui.navigation

import com.emarketing_thripitaka.application.R

sealed class BottomNavItem(
    val route: String, 
    val iconRes: Int, 
    val label: String
) {
    object Home : BottomNavItem("home", R.drawable.ic_solar_home_smile, "මුල් පිටුව")
    object Downloads : BottomNavItem("downloads", R.drawable.ic_solar_document_text, "බාගැනීම්")
    object Services : BottomNavItem("services", R.drawable.ic_solar_user_id, "සේවාවන්")
    object About : BottomNavItem("about", R.drawable.ic_solar_user_circle, "අප ගැන")
}
