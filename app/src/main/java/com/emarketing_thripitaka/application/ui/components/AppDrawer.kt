package com.emarketing_thripitaka.application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.emarketing_thripitaka.application.ui.navigation.DrawerNavItem
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.AppColors
import com.emarketing_thripitaka.application.ui.theme.ThripitakaPaperApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val drawerItems = listOf(
        DrawerNavItem.Home,
        DrawerNavItem.Downloads,
        DrawerNavItem.Services,
        DrawerNavItem.About
    )

    ModalDrawerSheet(
        drawerContainerColor = AppColors.surface(),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight(),
        windowInsets = WindowInsets.navigationBars
    ) {
        // Drawer Header
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/background_app.jpeg")
                        .build()
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.BottomStart)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(70.dp),
                    shadowElevation = 4.dp
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data("file:///android_asset/logo.png")
                                .build()
                        ),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "ත්‍රිපිටක විභාග Past Papers",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "ශ්‍රී ලංකා රාමඤ්ඤය මහා නිකාය",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "ශ්‍රී ශාසනාලංකාර මහා විහාරය කුලියාපිටිය.",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Drawer Items
        drawerItems.forEach { item ->
            DrawerItem(
                icon = painterResource(id = item.iconRes),
                label = item.label,
                selected = currentRoute == item.route,
                onClick = {
                    scope.launch { drawerState.close() }
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp), 
            color = AppColors.unselected().copy(alpha = 0.2f)
        )
        
        // Improved Footer
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Powered By",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AppColors.unselected().copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data("file:///android_asset/Our_Logo.png")
                                .build()
                        ),
                        contentDescription = "Our Logo",
                        modifier = Modifier.height(32.dp),
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Column {
                        Text(
                            text = "E-Marketing Paradice",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AppColors.primary()
                            )
                        )
                        Text(
                            text = "Design Lab",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = AppColors.unselected().copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: Painter, 
    label: String, 
    selected: Boolean, 
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { 
            Icon(
                painter = icon, 
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            ) 
        },
        label = { 
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ) 
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = AppColors.primary().copy(alpha = 0.12f),
            unselectedContainerColor = Color.Transparent,
            selectedTextColor = AppColors.primary(),
            unselectedTextColor = AppColors.unselected(),
            selectedIconColor = AppColors.primary(),
            unselectedIconColor = AppColors.unselected()
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .height(56.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AppDrawerPreview() {
    ThripitakaPaperApplicationTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Open)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope
                )
            }
        ) {
            Box(Modifier.fillMaxSize())
        }
    }
}
