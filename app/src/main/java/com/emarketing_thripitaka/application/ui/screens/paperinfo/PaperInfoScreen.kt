package com.emarketing_thripitaka.application.ui.screens.paperinfo

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.emarketing_thripitaka.application.R
import com.emarketing_thripitaka.application.ViewModelFactory
import com.emarketing_thripitaka.application.ui.components.CustomButton
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperInfoScreen(
    navController: NavController,
    exam: String,
    category: String,
    paperTitle: String,
    paperLink: String,
    part: String?,
    year: String?,
    viewModel: PaperInfoViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    val decodedTitle = remember(paperTitle) { Screen.decode(paperTitle) }
    val decodedCategory = remember(category) { Screen.decode(category) }
    val decodedExam = remember(exam) { Screen.decode(exam) }
    val decodedLink = remember(paperLink) { Screen.decode(paperLink) }
    val decodedYear = remember(year) { if (year == "null" || year == null) null else Screen.decode(year) }
    val decodedPart = remember(part) { if (part == "null" || part == null) null else Screen.decode(part) }

    LaunchedEffect(decodedLink) {
        viewModel.initUrl(decodedLink)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Paper Details", 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = if (isDark) Color.White else Color.Black)
                    }
                },
                actions = {
                    Icon(
                        Icons.Default.Eco, 
                        null, 
                        tint = AppColors.primary(), 
                        modifier = Modifier.padding(end = 16.dp).size(28.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    if (isDark) listOf(AppColors.background(), Color(0xFF0A0A0A)) 
                    else listOf(BackgroundLightStart, Color(0xFFFFEAC6))
                ))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Feature Card
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(24.dp))) {
                    Image(
                        painter = rememberAsyncImagePainter("file:///android_asset/background_app.jpeg"),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))
                    ).padding(20.dp), contentAlignment = Alignment.BottomStart) {
                        Column {
                            Text(decodedTitle, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Text(decodedCategory, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Metadata Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.surface()),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        InfoRow(painterResource(id = R.drawable.ic_solar_medal_ribbon_star), "විභාගය", decodedExam)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AppColors.unselected().copy(0.1f))
                        InfoRow(painterResource(id = R.drawable.ic_solar_documents), "කාණ්ඩය", decodedCategory)
                        if (decodedYear != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AppColors.unselected().copy(0.1f))
                            InfoRow(painterResource(id = R.drawable.ic_solar_document_text), "වර්ෂය", decodedYear)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons
                val isDownloaded = uiState.localPath != null
                
                if (uiState.isCheckingDownload) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppColors.primary())
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        CustomButton(
                            title = if (isDownloaded) "ප්‍රශ්න පත්‍රය විවෘත කරන්න (Open Paper)" else "ප්‍රශ්න පත්‍රය බලන්න (View Online)",
                            icon = { 
                                if (isDownloaded) {
                                    Icon(Icons.Default.MenuBook, null, tint = Color.White)
                                } else {
                                    Icon(Icons.Default.Visibility, null, tint = Color.White)
                                }
                            },
                            onPress = {
                                val pathToOpen = uiState.localPath ?: decodedLink
                                navController.navigate(Screen.PdfViewer.createRoute(pathToOpen, decodedTitle, decodedYear, decodedPart))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!isDownloaded) {
                            OutlinedButton(
                                onClick = {
                                    if (!uiState.isDownloading) {
                                        viewModel.downloadPaper(decodedLink, decodedTitle, decodedCategory, decodedExam, decodedYear, decodedPart)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(18.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.primary()),
                                enabled = !uiState.isDownloading
                            ) {
                                if (uiState.isDownloading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AppColors.primary(), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("බාගත වෙමින් පවතී... (${(uiState.downloadProgress * 100).toInt()}%)", color = AppColors.primary())
                                } else {
                                    Icon(Icons.Default.FileDownload, null, tint = AppColors.primary())
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("ප්‍රශ්න පත්‍රය බාගත කරන්න (Download)", color = AppColors.primary())
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("මෙම ප්‍රශ්න පත්‍රය සාර්ථකව බාගත කර ඇත", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = {
                                    try {
                                        val file = File(uiState.localPath!!)
                                        if (file.exists()) {
                                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "application/pdf"
                                                putExtra(Intent.EXTRA_STREAM, uri)
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "ප්‍රශ්න පත්‍රය බෙදාගන්න"))
                                        }
                                    } catch (e: Exception) {
                                        // Error handling for share
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Share, null, tint = AppColors.primary())
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ප්‍රශ්න පත්‍රය බෙදාගන්න (Share Paper)", color = AppColors.primary())
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun InfoRow(painter: androidx.compose.ui.graphics.painter.Painter, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(painter, null, tint = AppColors.primary(), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaperInfoScreenPreview() {
    ThripitakaPaperApplicationTheme {
        PaperInfoScreen(
            navController = rememberNavController(),
            exam = "Pravachana",
            category = "Preliminary",
            paperTitle = "Dhamma",
            paperLink = "https://example.com/paper.pdf",
            part = "1",
            year = "2023"
        )
    }
}
