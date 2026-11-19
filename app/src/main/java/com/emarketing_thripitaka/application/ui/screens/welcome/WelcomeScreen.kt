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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emarketing_thripitaka.application.R
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun WelcomeScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3500.milliseconds)
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Welcome.route) { inclusive = true }
        }
    }

    WelcomeScreenContent(startAnimation = startAnimation)
}

@Composable
fun WelcomeScreenContent(startAnimation: Boolean) {
    val isDark = isSystemInDarkTheme()
    val isPreview = LocalInspectionMode.current
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation || isPreview) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation || isPreview) 1f else 0.7f,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "scale"
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "ත්‍රිපිටක විභාගය",
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
                text = "Past Papers",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color.White else Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.alpha(alphaAnim)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Developed By E-Marketing Paradice",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = (if (isDark) Color.White else Color.Black),
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ThripitakaPaperApplicationTheme {
        WelcomeScreenContent(startAnimation = true)
    }
}
