package com.emarketing_thripitaka.application.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.emarketing_thripitaka.application.ui.components.AboutCard
import com.emarketing_thripitaka.application.ui.components.SocialButton
import com.emarketing_thripitaka.application.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope = rememberCoroutineScope()
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val iconTint = if (isDark) Color.White else Color.Black

    val gradientColors = if (isDark) {
        listOf(AppColors.background(), Color(0xFF0A0A0A))
    } else {
        listOf(AppColors.background(), Color(0xFFFFEAC6))
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "අප ගැන",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = iconTint
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = AppColors.primary(),
                        modifier = Modifier.padding(end = 16.dp).size(28.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(innerPadding)
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Section
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(AppColors.surface()),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data("file:///android_asset/logo.png")
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(75.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "ත්‍රිපිටක විභාග Past Papers",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "ශ්‍රී ලංකා රාමඤ්ඤය මහා නිකාය",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = AppColors.primary(),
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Info Card
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AboutCard(
                        title = "App Information",
                        icon = Icons.Default.Info,
                        iconColor = AppColors.primary(),
                        content = "මෙම යෙදුම ශ්‍රී ලංකා රාමඤ්ඤය මහා නිකාය මඟින් පවත්වනු ලබන ශ්‍රී ලංකා ත්‍රිපිටක විභාගයට පෙනී සිටින සියලු දෙනාටම අවශ්‍ය පසුගිය ප්‍රශ්න පත්‍ර ඉතා පහසුවෙන් ලබා ගැනීමට නිර්මාණය කර ඇත. මෙය ඔබගේ අධ්‍යාපන කටයුතු වඩාත් පහසු කිරීමේ අරමුණින් ඉදිරිපත් කරමු.",
                        isDark = isDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Developer Card
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AboutCard(
                        title = "Developer Profile",
                        icon = Icons.Default.Code,
                        iconColor = Color(0xFF3B82F6),
                        content = "developed by E-Marketing Paradice Design Lab. We specialize in building modern, user-friendly digital experiences for the Sri Lankan community.",
                        isDark = isDark,
                        actions = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SocialButton(
                                    label = "Contact Us (WhatsApp)",
                                    icon = Icons.Default.Call,
                                    isDark = isDark,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/94763460514"))
                                        context.startActivity(intent)
                                    }
                                )
                                SocialButton(
                                    label = "Visit Website",
                                    icon = Icons.Default.Language,
                                    isDark = isDark,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://emarketingparadice.lk"))
                                        context.startActivity(intent)
                                    }
                                )
                                SocialButton(
                                    label = "Send Email",
                                    icon = Icons.Default.Mail,
                                    isDark = isDark,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:emarketingparadice@gmail.com")
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data("file:///android_asset/Our_Logo.png")
                            .build()
                    ),
                    contentDescription = "Our Logo",
                    modifier = Modifier.height(45.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Footer
                Text(
                    text = "Version 1.0.1",
                    color = AppColors.unselected(),
                    fontSize = 14.sp
                )
                Text(
                    text = "© 2026 E-Marketing Paradise Design Lab",
                    color = AppColors.unselected().copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    ThripitakaPaperApplicationTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        AboutScreen(navController = navController, drawerState = drawerState)
    }
}
