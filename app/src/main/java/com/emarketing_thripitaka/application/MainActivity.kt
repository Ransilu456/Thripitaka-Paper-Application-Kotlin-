package com.emarketing_thripitaka.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.emarketing_thripitaka.application.ui.theme.ThripitakaPaperApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThripitakaPaperApplicationTheme {
                com.emarketing_thripitaka.application.ui.layout.AppLayout()
            }
        }
    }
}
