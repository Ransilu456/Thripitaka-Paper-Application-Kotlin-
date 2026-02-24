package com.emarketing_thripitaka.application.ui.screens.pdfviewer

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.emarketing_thripitaka.application.ViewModelFactory
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.AppColors
import com.emarketing_thripitaka.application.ui.theme.ThripitakaPaperApplicationTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    navController: NavController,
    paperLink: String,
    paperTitle: String,
    year: String?,
    part: String?,
    viewModel: PdfViewerViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val isDark = isSystemInDarkTheme()
    
    // Robust Decoding
    val decodedTitle = remember(paperTitle) { Screen.decode(paperTitle) }
    val decodedLink = remember(paperLink) { Screen.decode(paperLink) }
    val decodedPart = remember(part) { if (part == null || part == "null") null else Screen.decode(part) }
    val decodedYear = remember(year) { if (year == null || year == "null") null else Screen.decode(year) }
    
    val fullTitle = if (decodedPart != null) "$decodedTitle - Part $decodedPart" else decodedTitle
    val loadState by viewModel.loadState.collectAsState()

    LaunchedEffect(decodedLink) {
        viewModel.loadPdf(decodedLink)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = fullTitle, 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (decodedYear != null) {
                            Text(
                                text = decodedYear, 
                                fontSize = 12.sp, 
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.primary()
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppColors.background(),
                    scrolledContainerColor = AppColors.background()
                )
            )
        },
        containerColor = AppColors.background()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = loadState) {
                is PdfLoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppColors.primary())
                    }
                }
                is PdfLoadState.Success -> {
                    NativePdfViewer(file = state.file)
                }
                is PdfLoadState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message, 
                            color = MaterialTheme.colorScheme.error, 
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun NativePdfViewer(file: File) {
    val renderer = try {
        val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        PdfRenderer(input)
    } catch (e: Exception) {
        null
    }
    
    if (renderer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error opening PDF file")
        }
        return
    }
    
    val pageCount = renderer.pageCount

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(count = pageCount, key = { it }) { index ->
                val bitmap = remember(index) {
                    val page = renderer.openPage(index)
                    val width = (page.width * 2).toInt()
                    val height = (page.height * 2).toInt()
                    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    
                    val canvas = Canvas(bmp)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    bmp
                }

                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Page ${index + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )
            }
        }
        
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            color = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Total Pages: $pageCount", color = Color.White, fontSize = 12.sp)
            }
        }
    }

    DisposableEffect(renderer) {
        onDispose {
            renderer.close()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PdfViewerScreenPreview() {
    ThripitakaPaperApplicationTheme {
        PdfViewerScreen(
            navController = rememberNavController(),
            paperLink = "example.pdf",
            paperTitle = "Sample Paper",
            year = "2023",
            part = "1"
        )
    }
}
