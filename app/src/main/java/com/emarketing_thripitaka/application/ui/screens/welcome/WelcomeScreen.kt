package com.emarketing_thripitaka.application.ui.screens.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavController) {
    val isDark = isSystemInDarkTheme()
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3500)
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Welcome.route) { inclusive = true }
        }
    }

    val gradientColors = if (isDark) {
        listOf(BackgroundDarkStart, BackgroundDarkEnd)
    } else {
        listOf(BackgroundLightStart, BackgroundLightEnd)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .systemBarsPadding()
    ) {
        // Main Content (Logo and Title)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter("file:///android_asset/logo.png"),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "ත්‍රිපිටක විභාග Past Papers",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.alpha(alphaAnim)
            )
            
            Text(
                text = "ශ්‍රී ලංකා රාමඤ්ඤය මහා නිකායේ ත්‍රිපිටක විභාග සඳහා",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .alpha(alphaAnim)
            )
        }

        // Bottom Details (Branding and Version)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Powered By",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.4f),
                    letterSpacing = 2.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "E-Marketing Paradice",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.primary(),
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Version 1.0.1",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    val navController = rememberNavController()
    WelcomeScreen(navController = navController)
}
