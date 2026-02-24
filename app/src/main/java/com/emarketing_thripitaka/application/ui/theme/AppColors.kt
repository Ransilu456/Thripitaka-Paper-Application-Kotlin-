package com.emarketing_thripitaka.application.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    // Light Mode Tokens
    private val LightBackground = Color(0xFFF7F4EF)
    private val LightPrimary = Color(0xFFF4B400)
    private val LightPressed = Color(0xFFF48400)
    private val LightUnselected = Color(0xFF696969)
    private val LightSurface = Color(0xFFFFFFFF)

    // Dark Mode Tokens
    private val DarkBackground = Color(0xFF000000)
    private val DarkPrimary = Color(0xFFE6AA00)
    private val DarkPressed = Color(0xFFCC9600)
    private val DarkUnselected = Color(0xFF888888)
    private val DarkSurface = Color(0xFF1A1A1A)

    @Composable
    fun primary() = if (isSystemInDarkTheme()) DarkPrimary else LightPrimary

    @Composable
    fun background() = if (isSystemInDarkTheme()) DarkBackground else LightBackground

    @Composable
    fun surface() = if (isSystemInDarkTheme()) DarkSurface else LightSurface

    @Composable
    fun unselected() = if (isSystemInDarkTheme()) DarkUnselected else LightUnselected

    @Composable
    fun pressed() = if (isSystemInDarkTheme()) DarkPressed else LightPressed
}
